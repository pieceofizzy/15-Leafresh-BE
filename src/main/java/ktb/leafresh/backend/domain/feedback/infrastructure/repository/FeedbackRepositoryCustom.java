package ktb.leafresh.backend.domain.feedback.infrastructure.repository;

import ktb.leafresh.backend.domain.feedback.domain.entity.Feedback;

import java.time.LocalDateTime;
import java.util.Optional;

public interface FeedbackRepositoryCustom {
    Optional<Feedback> findTodayFeedback(Long memberId);
    boolean existsPendingFeedbackRequest(Long memberId, LocalDateTime date);
}
