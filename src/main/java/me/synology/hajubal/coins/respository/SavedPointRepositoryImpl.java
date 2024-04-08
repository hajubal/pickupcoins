package me.synology.hajubal.coins.respository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.synology.hajubal.coins.entity.SavedPoint;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static me.synology.hajubal.coins.entity.QCookie.cookie1;
import static me.synology.hajubal.coins.entity.QSavedPoint.savedPoint;
import static me.synology.hajubal.coins.entity.QSiteUser.siteUser;

@RequiredArgsConstructor
public class SavedPointRepositoryImpl implements SavedPointRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SavedPoint> findBySiteUser(Long siteUserId, int dayBefore) {
        return jpaQueryFactory.selectFrom(savedPoint)
                    .leftJoin(savedPoint.cookie, cookie1)
                    .leftJoin(cookie1.siteUser, siteUser)
                .where(siteUser.id.eq(siteUserId)
                        .and(savedPoint.createdDate.between(LocalDateTime.now().minusDays(dayBefore).with(LocalTime.MIN)
                                , LocalDateTime.now().with(LocalTime.MIN))))
                .fetch();
    }
}
