package ktb.leafresh.backend.domain.feedback.presentation.controller;

import ktb.leafresh.backend.domain.feedback.application.service.FeedbackReadService;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.global.response.ApiResponse;
import ktb.leafresh.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/feedback")
@Slf4j
public class FeedbackReadController {

    private final FeedbackReadService feedbackReadService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getFeedback(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();

        log.info("[AI_FEEDBACK] 피드백 조회 요청: memberId = {}", memberId);

        FeedbackResponseDto existingFeedback = feedbackReadService.findTodayFeedbackOrRequestAi(memberId);

        if (existingFeedback != null) {
            return ResponseEntity.ok(ApiResponse.success("기존 피드백이 존재하여 반환합니다.", existingFeedback));
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success("피드백 요청이 정상적으로 접수되었습니다. 결과는 곧 제공됩니다.", null));
    }
}
