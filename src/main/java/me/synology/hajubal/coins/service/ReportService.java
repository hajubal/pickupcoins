package me.synology.hajubal.coins.service;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class ReportService {

    private final PointUrlRepository pointUrlRepository;

    private final SlackService slackService;

    private final SiteUserRepository siteUserRepository;

    public void report() {
        //신규로 등록된 URL
        List<PointUrl> pointUrls = pointUrlRepository.findByCreatedDateBetween(
                LocalDateTime.now().minusDays(1).with(LocalDateTime.MIN),
                LocalDateTime.now().with(LocalDateTime.MIN));

        log.info("Point urls size: {}", pointUrls.size());

        //수집 성공한 URL

        //현재 로그 인/아웃된 cookie

        //정보 알림 전송
        String message = MessageBuilder.builder()
                .urlCount(pointUrls.size())
                .build().format();

        //FIXME 사이트 사용자들 기준으로 로그 조회 해서 슬랙 발송되도록 수정 필요
        SiteUser siteUser = siteUserRepository.findAll().get(0);

        slackService.sendMessage("", message);
    }

    static class MessageBuilder {
        private final int urlCount;
        private final int successCount;
        private final int totalCookieCount;
        private final int logoutCookieCount;

        private static final String FORMAT = """
        - 수집한 URL: %d 개
        - 수집 성공한 URL: %d 개
        - 전체 쿠키 수(로그아웃 수): %d (%d)
        """;

        @Builder
        public MessageBuilder(int urlCount, int successCount, int totalCookieCount, int logoutCookieCount) {
            this.urlCount = urlCount;
            this.successCount = successCount;
            this.totalCookieCount = totalCookieCount;
            this.logoutCookieCount = logoutCookieCount;
        }

        public String format() {
            return FORMAT.formatted(this.urlCount, this.successCount, this.totalCookieCount, this.logoutCookieCount);
        }
    }

}
