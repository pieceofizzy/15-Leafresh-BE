package ktb.leafresh.backend.domain.feedback.domain.model;

import ktb.leafresh.backend.domain.feedback.presentation.dto.request.*;
import lombok.Builder;

import java.util.List;

@Builder
public record WeeklyFeedbackPayload(
        Long memberId,
        List<PersonalChallengeSubmissionDto> personalChallenges,
        List<GroupChallengeSubmissionDto> groupChallenges
) {
    public static WeeklyFeedbackPayload from(FeedbackRequestDto dto) {
        return WeeklyFeedbackPayload.builder()
                .memberId(dto.memberId())
                .personalChallenges(dto.personalChallenges())
                .groupChallenges(dto.groupChallenges())
                .build();
    }
}
