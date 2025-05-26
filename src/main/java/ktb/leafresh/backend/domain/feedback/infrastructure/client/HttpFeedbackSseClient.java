package ktb.leafresh.backend.domain.feedback.infrastructure.client;

import jakarta.annotation.PostConstruct;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackSsePayload;
import ktb.leafresh.backend.domain.feedback.support.sink.FeedbackSinkManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Profile("!local")
@Slf4j
@RequiredArgsConstructor
public class HttpFeedbackSseClient implements FeedbackSseClient {

    private final FeedbackSinkManager sinkManager;

    @Value("${ai-server.base-url}")
    private String baseUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public void connect() {
        log.info("[AI_FEEDBACK] FastAPI SSE 연결 시도");

        webClient.get()
                .uri("/feedback/stream")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<FeedbackSsePayload>>() {})
                .doOnNext(event -> {
                    FeedbackSsePayload payload = event.data();
                    Long memberId = payload.getMemberId();
                    log.info("[AI_FEEDBACK] FastAPI 응답 수신 - memberId={}, content={}", memberId, payload.getContent());
                    sinkManager.push(memberId, payload.getContent());
                })
                .doOnError(error -> log.error("[AI_FEEDBACK] FastAPI SSE 연결 오류: {}", error.getMessage(), error))
                .doOnComplete(() -> log.info("[AI_FEEDBACK] FastAPI SSE 연결 종료"))
                .subscribe();
    }
}
