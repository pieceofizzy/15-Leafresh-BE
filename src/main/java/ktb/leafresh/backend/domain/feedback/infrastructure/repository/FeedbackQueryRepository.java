package ktb.leafresh.backend.domain.feedback.infrastructure.repository;

import ktb.leafresh.backend.domain.feedback.presentation.dto.request.PersonalChallengeSubmissionDto;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.GroupChallengeSubmissionDto;

import java.util.List;

public interface FeedbackQueryRepository {
    List<PersonalChallengeSubmissionDto> findPersonalChallengeSubmissions(Long memberId);
    List<GroupChallengeSubmissionDto> findGroupChallengeSubmissions(Long memberId);
}
