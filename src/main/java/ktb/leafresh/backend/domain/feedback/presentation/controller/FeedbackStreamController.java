package ktb.leafresh.backend.domain.feedback.presentation.controller;

import ktb.leafresh.backend.domain.feedback.application.service.FeedbackStreamService;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.global.response.ApiResponse;
import ktb.leafresh.backend.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members/feedback")
@CrossOrigin(origins = "*") // 필요 시 특정 도메인 설정
public class FeedbackStreamController {

    private final FeedbackStreamService feedbackStreamService;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<ApiResponse<FeedbackResponseDto>>> streamFeedback(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMemberId();
        log.info("[AI_FEEDBACK] SSE 연결 요청 - memberId={}", memberId);
        return feedbackStreamService.getFeedbackStream(memberId);
    }
}
