package ktb.leafresh.backend.domain.verification.infrastructure.client;

import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSsePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
@Profile("local")
public class FakeAiVerificationSseClient implements AiVerificationSseClient {

    @Override
    public Flux<VerificationSsePayload> getFlux() {
        log.warn("[Fake SSE 클라이언트] 로컬 환경에서는 SSE 스트리밍을 사용하지 않습니다.");
        return Flux.never();
    }
}
