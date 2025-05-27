package ktb.leafresh.backend.domain.verification.presentation.controller;

import ktb.leafresh.backend.domain.verification.infrastructure.stream.AiVerificationStreamDispatcher;
import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSsePayload;
import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSseResponseDto;
import ktb.leafresh.backend.global.common.entity.enums.ChallengeType;
import ktb.leafresh.backend.global.exception.CustomException;
import ktb.leafresh.backend.global.exception.GlobalErrorCode;
import ktb.leafresh.backend.global.response.ApiResponse;
import ktb.leafresh.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/challenges/personal")
@RequiredArgsConstructor
public class PersonalChallengeVerificationSubmitSseController {

    private final AiVerificationStreamDispatcher dispatcher;

    @GetMapping("/{challengeId}/verification/result/stream")
    public Flux<ServerSentEvent<ApiResponse<VerificationSseResponseDto>>> streamPersonalVerificationResult(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long challengeId
    ) {
        if (userDetails == null) {
            log.warn("[개인 인증 SSE 수신 실패] 인증 정보가 없습니다.");
            throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
        }

        Long memberId = userDetails.getMemberId();
        log.info("[개인 인증 SSE 구독 요청] challengeId={}, memberId={}", challengeId, memberId);

        return dispatcher.subscribe(memberId, challengeId, ChallengeType.PERSONAL)
                .map(response -> ServerSentEvent.<ApiResponse<VerificationSseResponseDto>>builder()
                        .data(response)
                        .build());
    }
}
