package ktb.leafresh.backend.domain.feedback.infrastructure.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.PersonalChallengeSubmissionDto;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.GroupChallengeSubmissionDto;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.GroupChallengeSubmissionDto.SubmissionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FeedbackQueryRepositoryImpl implements FeedbackQueryRepository {

    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<PersonalChallengeSubmissionDto> findPersonalChallengeSubmissions(Long memberId) {
        return em.createQuery("""
            SELECT new ktb.leafresh.backend.domain.feedback.presentation.dto.request.PersonalChallengeSubmissionDto(
                pc.id, pc.title,
                CAST(CASE WHEN v.status = 'SUCCESS' THEN true ELSE false END AS boolean)
            )
            FROM PersonalChallengeVerification v
            JOIN v.personalChallenge pc
            WHERE v.member.id = :memberId
              AND pc.deletedAt IS NULL
              AND v.deletedAt IS NULL
              AND v.createdAt IS NOT NULL
        """, PersonalChallengeSubmissionDto.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    @Override
    public List<GroupChallengeSubmissionDto> findGroupChallengeSubmissions(Long memberId) {
        List<Object[]> resultList = em.createQuery("""
            SELECT
                gc.id,
                gc.title,
                gc.startDate,
                gc.endDate,
                v.status = 'SUCCESS',
                v.createdAt
            FROM GroupChallengeParticipantRecord pr
            JOIN pr.groupChallenge gc
            JOIN pr.verifications v
            WHERE pr.member.id = :memberId
              AND gc.deletedAt IS NULL
              AND pr.deletedAt IS NULL
              AND v.deletedAt IS NULL
              AND v.createdAt IS NOT NULL
        """, Object[].class)
                .setParameter("memberId", memberId)
                .getResultList();

        Map<Long, GroupChallengeSubmissionDto> groupMap = new LinkedHashMap<>();

        for (Object[] row : resultList) {
            Long id = (Long) row[0];
            String title = (String) row[1];
            LocalDateTime start = (LocalDateTime) row[2];
            LocalDateTime end = (LocalDateTime) row[3];
            Boolean isSuccess = (Boolean) row[4];
            LocalDateTime submittedAt = (LocalDateTime) row[5];

            groupMap
                    .computeIfAbsent(id, key -> new GroupChallengeSubmissionDto(
                            id, title, start, end, new ArrayList<>()
                    ))
                    .submissions()
                    .add(new SubmissionDto(isSuccess, submittedAt));
        }

        return new ArrayList<>(groupMap.values());
    }
}
