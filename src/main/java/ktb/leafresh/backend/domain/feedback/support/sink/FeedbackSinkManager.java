package ktb.leafresh.backend.domain.feedback.support.sink;

import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.global.response.ApiResponse;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FeedbackSinkManager {

    private final Map<Long, Sinks.Many<ServerSentEvent<ApiResponse<FeedbackResponseDto>>>> sinkMap = new ConcurrentHashMap<>();

    public Sinks.Many<ServerSentEvent<ApiResponse<FeedbackResponseDto>>> getSink(Long memberId) {
        return sinkMap.computeIfAbsent(memberId, id -> Sinks.many().multicast().onBackpressureBuffer());
    }

    public void push(Long memberId, ApiResponse<FeedbackResponseDto> data) {
        var sink = sinkMap.get(memberId);
        if (sink != null) {
            sink.tryEmitNext(ServerSentEvent.builder(data).build());
        }
    }

    public void removeSink(Long memberId) {
        sinkMap.remove(memberId);
    }
}
