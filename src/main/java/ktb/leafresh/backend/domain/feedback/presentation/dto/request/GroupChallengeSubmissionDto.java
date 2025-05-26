package ktb.leafresh.backend.domain.feedback.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record GroupChallengeSubmissionDto(
        Long id,
        String title,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<SubmissionDto> submissions
) {
    public record SubmissionDto(
            boolean isSuccess,
            LocalDateTime submittedAt
    ) {}
}
