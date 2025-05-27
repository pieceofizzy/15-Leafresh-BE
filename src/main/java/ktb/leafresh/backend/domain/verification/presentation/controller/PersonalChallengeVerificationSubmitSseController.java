package ktb.leafresh.backend.domain.verification.presentation.controller;

import ktb.leafresh.backend.domain.verification.infrastructure.stream.AiVerificationStreamDispatcher;
import ktb.leafresh.backend.domain.verification.presentation.dto.response.VerificationSsePayload;
import ktb.leafresh.backend.global.exception.CustomException;
import ktb.leafresh.backend.global.exception.GlobalErrorCode;
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
    public Flux<ServerSentEvent<VerificationSsePayload>> streamPersonalVerificationResult(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long challengeId
    ) {
        if (userDetails == null) {
            log.warn("[개인 인증 SSE 수신 실패] 인증 정보가 없습니다.");
            throw new CustomException(GlobalErrorCode.UNAUTHORIZED);
        }

        Long memberId = userDetails.getMemberId();
        log.info("[개인 인증 SSE 구독 요청] challengeId={}, memberId={}", challengeId, memberId);

        return dispatcher.subscribe(memberId, challengeId)
                .map(data -> ServerSentEvent.<VerificationSsePayload>builder().data(data).build());
    }
}
