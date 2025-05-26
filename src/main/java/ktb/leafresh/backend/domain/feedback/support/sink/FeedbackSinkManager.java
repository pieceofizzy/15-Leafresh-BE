package ktb.leafresh.backend.domain.feedback.support.sink;

import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FeedbackSinkManager {

    private final Map<Long, Sinks.Many<ServerSentEvent<String>>> sinkMap = new ConcurrentHashMap<>();

    public Sinks.Many<ServerSentEvent<String>> getSink(Long memberId) {
        return sinkMap.computeIfAbsent(memberId,
                id -> Sinks.many().multicast().onBackpressureBuffer());
    }

    public void push(Long memberId, String data) {
        Sinks.Many<ServerSentEvent<String>> sink = sinkMap.get(memberId);
        if (sink != null) {
            sink.tryEmitNext(ServerSentEvent.builder(data).build());
        }
    }

    public void removeSink(Long memberId) {
        sinkMap.remove(memberId);
    }
}
