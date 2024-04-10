package me.synology.hajubal.coins.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 포인트 배치 작업 결과 리포팅 서비스
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class ReportService {

    private final SlackService slackService;

    private final SiteUserRepository siteUserRepository;

    private final PointUrlService pointUrlService;

    private final SavedPointService savedPointService;

    private final CookieService cookieService;

    public void report() {
        //신규로 등록된 URL
        List<PointUrl> pointUrls = pointUrlService.findPointUrl(1);

        log.info("Point urls size: {}", pointUrls.size());

        List<SiteUser> activeSiteUser = siteUserRepository.findAllByActiveIsTrue();

        for (SiteUser siteUser : activeSiteUser) {
            //수집 성공한 포인트
            List<SavedPoint> savedPoints = savedPointService.findSavedPoint(siteUser.getId(), 1);

            int successCount = savedPoints.size();

            int dayAmount = savedPoints.stream().mapToInt(SavedPoint::getAmount).sum();

            //현재 로그 인/아웃된 cookie
            List<Cookie> cookies = cookieService.getAll(siteUser.getId());

            long invalidCookieCount = cookies.stream().filter(cookie -> cookie.getIsValid().equals(Boolean.FALSE)).count();

            //정보 알림 전송
            String message = MessageBuilder.builder()
                    .urlCount(pointUrls.size())
                    .successCount(successCount)
                    .amount(dayAmount)
                    .totalCookieCount(cookies.size())
                    .logoutCookieCount((int)invalidCookieCount)
                    .build().format();

            log.info("SiteUser info: {}", siteUser);

            slackService.sendMessage(siteUser.getSlackWebhookUrl(), message);
        }

    }

    public static class MessageBuilder {
        private final int urlCount;
        private final int successCount;
        private final int totalCookieCount;
        private final int logoutCookieCount;
        private final int amount;

        private static final String FORMAT = """
        - 수집한 URL: %d 개
        - 수집 성공한 URL: %d 개
        - 전체 쿠키 수(로그아웃 수): %d (%d)
        - 수집한 금액: %d
        """;

        @Builder
        public MessageBuilder(int urlCount, int successCount, int totalCookieCount, int logoutCookieCount, int amount) {
            this.urlCount = urlCount;
            this.successCount = successCount;
            this.totalCookieCount = totalCookieCount;
            this.logoutCookieCount = logoutCookieCount;
            this.amount = amount;
        }

        public String format() {
            return FORMAT.formatted(this.urlCount, this.successCount, this.totalCookieCount, this.logoutCookieCount
                    , this.amount);
        }
    }

}
