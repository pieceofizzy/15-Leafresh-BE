package ktb.leafresh.backend.domain.feedback.presentation.dto.request;

public record PersonalChallengeSubmissionDto(
        Long id,
        String title,
        boolean isSuccess
) {}
