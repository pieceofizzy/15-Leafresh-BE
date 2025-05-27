package ktb.leafresh.backend.domain.feedback.application.service;

import ktb.leafresh.backend.domain.feedback.infrastructure.client.FeedbackSseClient;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.domain.feedback.support.sink.FeedbackSinkManager;
import ktb.leafresh.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackStreamService {

    private final FeedbackSseClient feedbackSseClient;
    private final FeedbackSinkManager sinkManager;

    private final AtomicBoolean connected = new AtomicBoolean(false);

    public Flux<ServerSentEvent<ApiResponse<FeedbackResponseDto>>> getFeedbackStream(Long memberId) {
        if (connected.compareAndSet(false, true)) {
            log.info("[AI_FEEDBACK] FastAPI SSE 최초 연결 시도");
            feedbackSseClient.connect();
        } else {
            log.debug("[AI_FEEDBACK] FastAPI SSE 이미 연결됨 - 중복 연결 방지");
        }

        return sinkManager.getSink(memberId)
                .asFlux()
                .doOnCancel(() -> {
                    log.info("[AI_FEEDBACK] SSE 연결 해제됨 - memberId={}", memberId);
                    sinkManager.removeSink(memberId);
                })
                .doOnError(e -> log.error("[AI_FEEDBACK] SSE 스트림 중 오류 발생 - memberId={}, error={}", memberId, e.getMessage(), e));
    }
}
