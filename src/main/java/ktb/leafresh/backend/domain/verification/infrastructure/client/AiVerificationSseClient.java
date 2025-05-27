package ktb.leafresh.backend.domain.verification.infrastructure.client;

import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSsePayload;
import reactor.core.publisher.Flux;

public interface AiVerificationSseClient {
    Flux<VerificationSsePayload> getFlux();
}
