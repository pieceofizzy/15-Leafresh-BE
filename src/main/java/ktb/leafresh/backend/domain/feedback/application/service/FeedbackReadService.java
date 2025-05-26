package ktb.leafresh.backend.domain.feedback.application.service;

import ktb.leafresh.backend.domain.feedback.domain.entity.Feedback;
import ktb.leafresh.backend.domain.feedback.infrastructure.repository.FeedbackRepository;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.FeedbackRequestDto;
import ktb.leafresh.backend.domain.feedback.presentation.dto.response.FeedbackResponseDto;
import ktb.leafresh.backend.domain.member.domain.entity.Member;
import ktb.leafresh.backend.domain.member.infrastructure.repository.MemberRepository;
import ktb.leafresh.backend.global.exception.CustomException;
import ktb.leafresh.backend.global.exception.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ktb.leafresh.backend.global.exception.FeedbackErrorCode;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackReadService {

    private final FeedbackRepository feedbackRepository;
    private final ChallengeHistoryQueryService challengeHistoryQueryService;
    private final FeedbackRequestService feedbackRequestService;
    private final MemberRepository memberRepository;

    public FeedbackResponseDto findTodayFeedbackOrRequestAi(Long memberId) {
        Optional<Feedback> todayFeedback = feedbackRepository.findTodayFeedback(memberId);

        if (todayFeedback.isPresent()) {
            return FeedbackResponseDto.of(memberId, todayFeedback.get().getContent());
        }

        boolean hasActivity = challengeHistoryQueryService.hasActivity(memberId);
        if (!hasActivity) {
            throw new CustomException(FeedbackErrorCode.NO_ACTIVITY_IN_PAST_WEEK);
        }

        // 중복 요청 방지
        boolean alreadyRequested = feedbackRepository.existsPendingFeedbackRequest(memberId, LocalDateTime.now());
        if (alreadyRequested) {
            throw new CustomException(FeedbackErrorCode.ALREADY_REQUESTED_TODAY);
        }

        FeedbackRequestDto dto = challengeHistoryQueryService.collectSubmissions(memberId);
        FeedbackResponseDto result = feedbackRequestService.sendFeedbackToAi(dto);

        String feedback = result.feedback();

        Member member = memberRepository.findById(dto.memberId())
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        Feedback feedbackEntity = Feedback.of(member, feedback);
        feedbackRepository.save(feedbackEntity);

        return FeedbackResponseDto.of(dto.memberId(), feedback);
    }
}
