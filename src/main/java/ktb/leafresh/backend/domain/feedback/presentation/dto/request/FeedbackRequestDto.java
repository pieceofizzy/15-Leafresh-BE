package ktb.leafresh.backend.domain.feedback.presentation.dto.request;

import java.util.List;

public record FeedbackRequestDto(
        Long memberId,
        List<PersonalChallengeSubmissionDto> personalChallenges,
        List<GroupChallengeSubmissionDto> groupChallenges
) {
    public boolean hasNoChallengeData() {
        return (personalChallenges == null || personalChallenges.isEmpty()) &&
                (groupChallenges == null || groupChallenges.isEmpty());
    }
}
