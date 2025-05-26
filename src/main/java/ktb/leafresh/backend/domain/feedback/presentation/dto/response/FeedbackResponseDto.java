package ktb.leafresh.backend.domain.feedback.presentation.dto.response;

public record FeedbackResponseDto(
        Long memberId,
        String feedback
) {
    public static FeedbackResponseDto of(Long memberId, String feedback) {
        return new FeedbackResponseDto(memberId, feedback);
    }
}
