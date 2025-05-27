package ktb.leafresh.backend.domain.verification.infrastructure.stream;

import jakarta.annotation.PostConstruct;
import ktb.leafresh.backend.domain.verification.application.service.VerificationResultProcessor;
import ktb.leafresh.backend.domain.verification.infrastructure.client.AiVerificationSseClient;
import ktb.leafresh.backend.domain.verification.presentation.dto.request.VerificationResultRequestDto;
import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSsePayload;
import ktb.leafresh.backend.global.common.entity.enums.ChallengeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiVerificationStreamDispatcher {

    private final VerificationResultProcessor verificationResultProcessor;
    private final AiVerificationSseClient sseClient;
    private final Map<String, Sinks.Many<VerificationSsePayload>> userSinkMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        sseClient.getFlux().subscribe(event -> {
            // 저장 로직: 상태 업데이트, 리워드 지급, 알림 생성
            processVerificationResult(event);

            // FE에게 결과 push
            String key = key(event.memberId(), event.challengeId());
            Sinks.Many<VerificationSsePayload> sink = userSinkMap.get(key);
            if (sink != null) {
                sink.tryEmitNext(event);
            }
        });
    }

    public Flux<VerificationSsePayload> subscribe(Long memberId, Long challengeId) {
        String key = key(memberId, challengeId);
        Sinks.Many<VerificationSsePayload> sink = Sinks.many().unicast().onBackpressureBuffer();
        userSinkMap.put(key, sink);
        return sink.asFlux();
    }

    private String key(Long memberId, Long challengeId) {
        return memberId + "_" + challengeId;
    }

    private void processVerificationResult(VerificationSsePayload event) {
        VerificationResultRequestDto dto = VerificationResultRequestDto.builder()
                .type(ChallengeType.valueOf(event.challengeType()))
                .memberId(event.memberId())
                .challengeId(event.challengeId())
                .date(LocalDateTime.now().toString())
                .result(event.result())
                .build();

        verificationResultProcessor.process(event.verificationId(), dto);
    }
}
