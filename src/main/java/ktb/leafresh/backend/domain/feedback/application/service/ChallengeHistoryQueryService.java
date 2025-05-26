package ktb.leafresh.backend.domain.feedback.application.service;

import ktb.leafresh.backend.domain.challenge.group.infrastructure.repository.GroupChallengeParticipantRecordRepository;
import ktb.leafresh.backend.domain.challenge.personal.infrastructure.repository.PersonalChallengeRepository;
import ktb.leafresh.backend.domain.feedback.infrastructure.repository.FeedbackQueryRepository;
import ktb.leafresh.backend.domain.feedback.presentation.dto.request.FeedbackRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChallengeHistoryQueryService {

    private final PersonalChallengeRepository personalChallengeRepository;
    private final GroupChallengeParticipantRecordRepository participantRecordRepository;
    private final FeedbackQueryRepository feedbackQueryRepository;

    public boolean hasActivity(Long memberId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        return personalChallengeRepository.existsSuccessInPastWeek(memberId, oneWeekAgo) ||
                participantRecordRepository.existsSuccessInPastWeek(memberId, oneWeekAgo);
    }

    public FeedbackRequestDto collectSubmissions(Long memberId) {
        var personal = feedbackQueryRepository.findPersonalChallengeSubmissions(memberId);
        var group = feedbackQueryRepository.findGroupChallengeSubmissions(memberId);
        return new FeedbackRequestDto(memberId, personal, group);
    }
}
