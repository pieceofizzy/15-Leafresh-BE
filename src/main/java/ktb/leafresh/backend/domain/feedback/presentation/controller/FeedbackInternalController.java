package ktb.leafresh.backend.domain.feedback.presentation.controller;

import jakarta.validation.Valid;
import ktb.leafresh.backend.domain.feedback.application.service.FeedbackRequestService;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.FeedbackRequestDto;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.global.exception.CustomException;
import ktb.leafresh.backend.global.exception.FeedbackErrorCode;
import ktb.leafresh.backend.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ai/feedback")
@Slf4j
public class FeedbackInternalController {

    private final FeedbackRequestService feedbackRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> requestFeedback(
            @RequestBody @Valid FeedbackRequestDto requestDto
    ) {
        log.info("[AI_FEEDBACK] 피드백 요청 시작: memberId = {}", requestDto.memberId());

        if (requestDto.memberId() == null) {
            throw new CustomException(FeedbackErrorCode.MISSING_MEMBER_ID);
        }

        if (requestDto.personalChallenges() == null && requestDto.groupChallenges() == null) {
            throw new CustomException(FeedbackErrorCode.INVALID_FORMAT);
        }

        if (requestDto.hasNoChallengeData()) {
            throw new CustomException(FeedbackErrorCode.NO_CHALLENGE_ACTIVITY);
        }

        FeedbackResponseDto result = feedbackRequestService.sendFeedbackToAi(requestDto);

        log.info("[AI_FEEDBACK] 피드백 요청 성공: memberId = {}", requestDto.memberId());

        return ResponseEntity.ok(ApiResponse.success(
                "주간 피드백이 성공적으로 생성되었습니다.",
                FeedbackResponseDto.of(requestDto.memberId(), result.feedback())
        ));
    }
}
