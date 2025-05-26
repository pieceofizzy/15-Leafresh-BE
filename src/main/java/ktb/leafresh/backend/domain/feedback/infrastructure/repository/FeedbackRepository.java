package ktb.leafresh.backend.domain.feedback.infrastructure.repository;

import ktb.leafresh.backend.domain.feedback.domain.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long>, FeedbackRepositoryCustom {
}
