package ktb.leafresh.backend.domain.verification.infrastructure.stream;

import jakarta.annotation.PostConstruct;
import ktb.leafresh.backend.domain.verification.application.service.VerificationResultProcessor;
import ktb.leafresh.backend.domain.verification.infrastructure.client.AiVerificationSseClient;
import ktb.leafresh.backend.domain.verification.presentation.dto.request.VerificationResultRequestDto;
import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSsePayload;
import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSseResponseDto;
import ktb.leafresh.backend.global.common.entity.enums.ChallengeStatus;
import ktb.leafresh.backend.global.common.entity.enums.ChallengeType;
import ktb.leafresh.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiVerificationStreamDispatcher {

    private final VerificationResultProcessor verificationResultProcessor;
    private final AiVerificationSseClient sseClient;

    private final Map<String, Sinks.Many<ApiResponse<VerificationSseResponseDto>>> sseSinkMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        sseClient.getFlux().subscribe(event -> {
            log.info("[1단계] SSE 이벤트 수신: verificationId={}, memberId={}, challengeId={}, type={}, result={}",
                    event.verificationId(), event.memberId(), event.challengeId(), event.challengeType(), event.result());

            // 2단계: 인증 결과 저장 및 리워드 처리
            log.info("[2단계] 인증 결과 처리 시작");
            processVerificationResult(event);
            log.info("[2단계] 인증 결과 처리 완료");

            // 3단계: Sink 조회 및 응답 전송
            String key = key(event.memberId(), event.challengeId());
            Sinks.Many<ApiResponse<VerificationSseResponseDto>> sink = sseSinkMap.get(key);

            if (sink != null) {
                VerificationSseResponseDto dto = VerificationSseResponseDto.fromBoolean(event.result());
                var response = ApiResponse.success(dto.message(), dto);
                sink.tryEmitNext(response);
                log.info("[3단계] SSE 응답 전송 완료 - key={}, status={}, message={}", key, dto.status(), dto.message());
            } else {
                log.warn("[3단계] SSE Sink 미존재로 전송 생략 - key={}", key);
            }
        });
    }

    public Flux<ApiResponse<VerificationSseResponseDto>> subscribe(Long memberId, Long challengeId, ChallengeType type) {
        String key = key(memberId, challengeId);
        Sinks.Many<ApiResponse<VerificationSseResponseDto>> sink = Sinks.many().unicast().onBackpressureBuffer();
        sseSinkMap.put(key, sink);

        // 1단계: 초기 상태 조회
        log.info("[초기 상태 조회] SSE 구독 요청 수신 - memberId={}, challengeId={}, type={}", memberId, challengeId, type);
        ChallengeStatus status = verificationResultProcessor.getCurrentStatus(memberId, challengeId, type);
        VerificationSseResponseDto dto = VerificationSseResponseDto.fromStatus(status);
        var initialResponse = ApiResponse.success(dto.message(), dto);

        // 2단계: 초기 응답 전송
        sink.tryEmitNext(initialResponse);
        log.info("[초기 응답 전송 완료] key={}, status={}, message={}", key, dto.status(), dto.message());

        // 3단계: ping 이벤트 스트림
        Flux<ApiResponse<VerificationSseResponseDto>> pingFlux = Flux
                .interval(Duration.ofSeconds(30))
                .map(i -> {
                    log.debug("[PING 전송] key={}", key);
                    return ApiResponse.success("ping", new VerificationSseResponseDto("PING", "ping"));
                });

        // 4단계: merge & 연결 종료 처리
        return Flux.merge(sink.asFlux(), pingFlux)
                .doFinally(signal -> {
                    log.info("[SSE 연결 종료] key={}, 종료 signal={}", key, signal);
                    sseSinkMap.remove(key);
                });
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

    private String key(Long memberId, Long challengeId) {
        return memberId + "_" + challengeId;
    }
}
