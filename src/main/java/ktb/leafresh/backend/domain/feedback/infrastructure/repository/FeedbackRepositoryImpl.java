package ktb.leafresh.backend.domain.feedback.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ktb.leafresh.backend.domain.feedback.domain.entity.Feedback;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepositoryCustom {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public Optional<Feedback> findTodayFeedback(Long memberId) {
        // 오늘 00:00:00 ~ 오늘 23:59:59
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);

        return em.createQuery("""
            SELECT f
            FROM Feedback f
            WHERE f.member.id = :memberId
              AND f.createdAt BETWEEN :start AND :end
              AND f.deletedAt IS NULL
        """, Feedback.class)
                .setParameter("memberId", memberId)
                .setParameter("start", startOfDay)
                .setParameter("end", endOfDay)
                .setMaxResults(1)
                .getResultStream()
                .findFirst();
    }

    @Override
    public boolean existsPendingFeedbackRequest(Long memberId, LocalDateTime since) {
        Long count = em.createQuery("""
            SELECT COUNT(f)
            FROM Feedback f
            WHERE f.member.id = :memberId
              AND f.createdAt >= :since
              AND f.deletedAt IS NULL
        """, Long.class)
                .setParameter("memberId", memberId)
                .setParameter("since", since)
                .getSingleResult();

        return count > 0;
    }
}
