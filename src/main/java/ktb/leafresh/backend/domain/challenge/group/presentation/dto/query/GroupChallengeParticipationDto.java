package ktb.leafresh.backend.domain.challenge.group.presentation.dto.query;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class GroupChallengeParticipationDto {
    private Long id;
    private String title;
    private String thumbnailUrl;
    private String startDate;
    private String endDate;
    private Long success;
    private Long total;
    private LocalDateTime createdAt;

    public GroupChallengeParticipationDto(
            Long id,
            String title,
            String thumbnailUrl,
            String startDate,
            String endDate,
            Long success,
            Long total,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.startDate = startDate;
        this.endDate = endDate;
        this.success = success;
        this.total = total;
        this.createdAt = createdAt;
    }
}
