package ktb.leafresh.backend.domain.feedback.infrastructure.client;

import ktb.leafresh.backend.domain.feedback.support.sink.FeedbackSinkManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
@Slf4j
@RequiredArgsConstructor
public class FakeFeedbackSseClient implements FeedbackSseClient {

    private final FeedbackSinkManager sinkManager;

    @Override
    public void connect() {
        log.info("[AI_FEEDBACK] 테스트용 SSE 응답 전송");

        // 요청된 memberId를 받아와야 제대로 작동합니다.
        Long testMemberId = 2L; // ← 지금 테스트 중인 memberId로 맞추기
        String fakeMessage = "[테스트 응답] 지난주에 텀블러 사용과 플로깅을 실천하셨습니다. 환경을 위한 실천, 응원합니다.";

        sinkManager.push(testMemberId, fakeMessage);
    }
}
