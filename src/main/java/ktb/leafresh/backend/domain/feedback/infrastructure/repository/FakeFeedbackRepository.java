package ktb.leafresh.backend.domain.feedback.infrastructure.repository;

import ktb.leafresh.backend.domain.feedback.domain.entity.Feedback;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("local")
public class FakeFeedbackRepository implements FeedbackRepositoryCustom {
    private final Map<Long, Feedback> map = new ConcurrentHashMap<>();
    private Long idSequence = 1L;

    @Override
    public Optional<Feedback> findTodayFeedback(Long memberId) {
        return Optional.empty(); // 항상 새 요청
    }

    @Override
    public boolean existsPendingFeedbackRequest(Long memberId, LocalDateTime date) {
        return false;
    }

    public Feedback save(Feedback feedback) {
        Feedback saved = Feedback.builder()
                .id(idSequence++)
                .member(feedback.getMember())
                .content(feedback.getContent())
                .build();
        map.put(saved.getId(), saved);
        return saved;
    }
}
