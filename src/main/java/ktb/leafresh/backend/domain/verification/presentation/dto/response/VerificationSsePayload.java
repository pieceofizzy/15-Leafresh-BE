package ktb.leafresh.backend.domain.verification.presentation.dto.response;

public record VerificationSsePayload(
        Long verificationId,
        Long memberId,
        Long challengeId,
        String challengeType, // "PERSONAL", "GROUP"
        boolean result
) {}
