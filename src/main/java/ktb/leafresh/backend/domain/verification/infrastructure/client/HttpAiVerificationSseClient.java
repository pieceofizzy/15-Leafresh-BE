package ktb.leafresh.backend.domain.verification.infrastructure.client;

import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSsePayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
@Component
@Profile("docker-local") // dev
public class HttpAiVerificationSseClient implements AiVerificationSseClient {

    private final WebClient aiServerWebClient;
    private final Sinks.Many<VerificationSsePayload> sink = Sinks.many().multicast().onBackpressureBuffer();

    public HttpAiVerificationSseClient(@Qualifier("aiServerWebClient") WebClient aiServerWebClient) {
        this.aiServerWebClient = aiServerWebClient;
        subscribeFromAi();
    }

    private void subscribeFromAi() {
        aiServerWebClient.get()
                .uri("/api/verifications/result/stream") // /api/verifications/{verificationId}/result/stream
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<VerificationSsePayload>>() {})
                .map(ServerSentEvent::data)
                .doOnNext(sink::tryEmitNext)
                .doOnError(e -> log.error("[SSE 연결 실패] {}", e.getMessage(), e))
                .subscribe();
    }

    @Override
    public Flux<VerificationSsePayload> getFlux() {
        return sink.asFlux();
    }
}
