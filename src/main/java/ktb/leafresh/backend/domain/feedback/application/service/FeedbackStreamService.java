package ktb.leafresh.backend.domain.feedback.application.service;

import ktb.leafresh.backend.domain.feedback.infrastructure.client.FeedbackSseClient;
import ktb.leafresh.backend.domain.feedback.support.sink.FeedbackSinkManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackStreamService {

    private final FeedbackSseClient feedbackSseClient;
    private final FeedbackSinkManager sinkManager;

    public Flux<ServerSentEvent<String>> getFeedbackStream(Long memberId) {
        feedbackSseClient.connect(); // FastAPI SSE 연결 시작
        return sinkManager.getSink(memberId)
                .asFlux()
                .doOnCancel(() -> {
                    log.info("[AI_FEEDBACK] SSE 연결 해제됨 - memberId={}", memberId);
                    sinkManager.removeSink(memberId);
                })
                .doOnError(e -> log.error("[AI_FEEDBACK] SSE 스트림 중 오류 발생: {}", e.getMessage(), e));
    }
}
