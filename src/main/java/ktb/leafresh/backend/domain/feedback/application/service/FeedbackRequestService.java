package ktb.leafresh.backend.domain.feedback.application.service;

import ktb.leafresh.backend.domain.feedback.domain.model.WeeklyFeedbackPayload;
import ktb.leafresh.backend.domain.feedback.infrastructure.client.FeedbackAiClient;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.FeedbackRequestDto;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.global.exception.CustomException;
import ktb.leafresh.backend.global.exception.FeedbackErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackRequestService {

    private final FeedbackAiClient feedbackAiClient;

    public FeedbackResponseDto sendFeedbackToAi(FeedbackRequestDto dto) {
        log.debug("[AI_FEEDBACK] FastAPI 전송 전 payload 생성");

        WeeklyFeedbackPayload payload = WeeklyFeedbackPayload.from(dto);

        log.debug("[AI_FEEDBACK] FastAPI로 요청 시작: {}", payload);

        try {
            String feedback = feedbackAiClient.requestWeeklyFeedback(payload);
            return new FeedbackResponseDto(dto.memberId(), feedback);
        } catch (Exception e) {
            log.error("[AI_FEEDBACK] 피드백 요청 실패", e);
            throw new CustomException(FeedbackErrorCode.FEEDBACK_SERVER_ERROR);
        }
    }
}
