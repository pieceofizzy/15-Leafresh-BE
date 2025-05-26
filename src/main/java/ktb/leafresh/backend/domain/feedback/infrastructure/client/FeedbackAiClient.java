package ktb.leafresh.backend.domain.feedback.infrastructure.client;

import ktb.leafresh.backend.domain.feedback.domain.model.WeeklyFeedbackPayload;

public interface FeedbackAiClient {
    String requestWeeklyFeedback(WeeklyFeedbackPayload payload);
}
