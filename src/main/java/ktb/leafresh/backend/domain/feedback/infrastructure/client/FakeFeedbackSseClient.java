package ktb.leafresh.backend.domain.feedback.infrastructure.client;

import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.domain.feedback.support.sink.FeedbackSinkManager;
import ktb.leafresh.backend.global.response.ApiResponse;
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
        log.info("[AI_FEEDBACK] 테스트용 SSE 응답 전송 시작");

        Long testMemberId = 2L; // 현재 테스트 대상 사용자 ID
        String fakeContent = "지난주에 텀블러 사용과 플로깅을 실천하셨습니다. 환경을 위한 실천, 응원합니다 🌿";

        FeedbackResponseDto responseDto = FeedbackResponseDto.of(testMemberId, fakeContent);
        ApiResponse<FeedbackResponseDto> apiResponse = ApiResponse.success("테스트 피드백 도착", responseDto);

        sinkManager.push(testMemberId, apiResponse);

        log.info("[AI_FEEDBACK] 테스트용 피드백 전송 완료 - memberId={}, content={}", testMemberId, fakeContent);
    }
}
