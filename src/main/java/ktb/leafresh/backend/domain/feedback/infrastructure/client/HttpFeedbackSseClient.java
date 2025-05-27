package ktb.leafresh.backend.domain.feedback.infrastructure.client;

import jakarta.annotation.PostConstruct;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackSsePayload;
import ktb.leafresh.backend.domain.feedback.support.sink.FeedbackSinkManager;
import ktb.leafresh.backend.global.response.ApiResponse;
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
                .uri("/api/members/feedback/result/stream")
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<FeedbackSsePayload>>() {})
                .doOnNext(event -> {
                    var payload = event.data();
                    if (payload == null) return;

                    var responseDto = FeedbackResponseDto.of(payload.getMemberId(), payload.getContent());
                    var apiResponse = ApiResponse.success("피드백 결과 수신 완료", responseDto);
                    sinkManager.push(payload.getMemberId(), apiResponse);

                    log.info("[AI_FEEDBACK] SSE 수신 완료 - memberId={}, content={}", payload.getMemberId(), payload.getContent());
                })
                .doOnError(e -> log.error("[AI_FEEDBACK] SSE 연결 오류: {}", e.getMessage(), e))
                .doOnComplete(() -> log.info("[AI_FEEDBACK] SSE 스트림 종료"))
                .subscribe();
    }
}
