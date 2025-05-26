package ktb.leafresh.backend.domain.feedback.infrastructure.client;

import ktb.leafresh.backend.domain.feedback.domain.model.WeeklyFeedbackPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@Slf4j
public class FakeFeedbackAiClient implements FeedbackAiClient {

    @Override
    public String requestWeeklyFeedback(WeeklyFeedbackPayload payload) {
        log.info("[AI_FEEDBACK] 테스트용 피드백 응답 반환 - memberId={}", payload.memberId());

        return String.format(
                "[테스트용 응답] 지난주에 %s 건의 활동을 기록하셨습니다. 환경을 위한 실천, 응원합니다!",
                payload.personalChallenges().size() + payload.groupChallenges().size()
        );
    }
}
