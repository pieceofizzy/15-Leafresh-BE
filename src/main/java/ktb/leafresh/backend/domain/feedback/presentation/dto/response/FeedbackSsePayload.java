package ktb.leafresh.backend.domain.feedback.presentation.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FeedbackSsePayload {

    private Long memberId;
    private String content;
}
