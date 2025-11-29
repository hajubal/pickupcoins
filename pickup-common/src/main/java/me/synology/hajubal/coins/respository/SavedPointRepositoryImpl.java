package me.synology.hajubal.coins.respository;

import com.querydsl.core.types.Order;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.synology.hajubal.coins.entity.SavedPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.querydsl.core.types.ExpressionUtils.count;
import static me.synology.hajubal.coins.entity.QCookie.cookie1;
import static me.synology.hajubal.coins.entity.QSavedPoint.savedPoint;
import static me.synology.hajubal.coins.entity.QSiteUser.siteUser;

@RequiredArgsConstructor
public class SavedPointRepositoryImpl implements SavedPointRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<SavedPoint> findBySiteUser(Long siteUserId, int dayBefore) {
        return jpaQueryFactory.selectFrom(savedPoint)
                    .leftJoin(savedPoint.cookie, cookie1).fetchJoin()
                    .leftJoin(cookie1.siteUser, siteUser).fetchJoin()
                .where(siteUser.id.eq(siteUserId)
                        .and(savedPoint.createdDate.between(LocalDateTime.now().minusDays(dayBefore).with(LocalTime.MIN)
                                , LocalDateTime.now().with(LocalTime.MIN))))
                .fetch();
    }

    public Page<SavedPoint> findAllBySiteUser(Long siteUserId, Pageable pageable) {
        List<SavedPoint> savedPoints = jpaQueryFactory.selectFrom(savedPoint)
                .leftJoin(savedPoint.cookie, cookie1).fetchJoin()
                .leftJoin(cookie1.siteUser, siteUser).fetchJoin()
                .where(siteUser.id.eq(siteUserId))
                .orderBy(savedPoint.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long size = jpaQueryFactory.select(count(savedPoint))
                .from(savedPoint)
                .leftJoin(savedPoint.cookie, cookie1)
                .leftJoin(cookie1.siteUser, siteUser)
                .where(siteUser.id.eq(siteUserId))
                .fetchOne();

        return new PageImpl<>(savedPoints, pageable, size);
    }
}
