package ktb.leafresh.backend.support.fixture;

import ktb.leafresh.backend.domain.member.domain.entity.Badge;
import ktb.leafresh.backend.domain.member.domain.entity.enums.BadgeType;

import java.util.ArrayList;

public class BadgeFixture {

    public static Badge of(String name) {
        return Badge.builder()
                .id(1L)
                .memberBadges(new ArrayList<>())
                .type(BadgeType.EVENT)
                .name(name)
                .condition("이벤트 인증 3회 성공 시 지급")
                .imageUrl("https://dummy.image/badge/" + name + ".png")
                .build();
    }
}
