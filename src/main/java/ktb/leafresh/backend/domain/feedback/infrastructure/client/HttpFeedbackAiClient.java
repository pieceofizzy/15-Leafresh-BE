package ktb.leafresh.backend.domain.feedback.infrastructure.client;

import ktb.leafresh.backend.domain.feedback.domain.model.WeeklyFeedbackPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@Profile("!local")
@Slf4j
public class HttpFeedbackAiClient implements FeedbackAiClient {

    private final WebClient webClient;

    public HttpFeedbackAiClient(@Value("${ai-server.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public String requestWeeklyFeedback(WeeklyFeedbackPayload payload) {
        try {
            return webClient.post()
                    .uri("/feedback")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (Exception e) {
            log.error("[AI_FEEDBACK] FastAPI 요청 실패: {}", e.getMessage(), e);
            throw e;
        }
    }
}
