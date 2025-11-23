package me.synology.hajubal.coins.service;

import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Cookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 쿠키 상태 변경 알림 서비스
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class CookieNotificationService {

    private final me.synology.hajubal.coins.service.CookieService cookieService;

    private final SlackService slackService;

    /**
     * 쿠키 무효화 및 Slack 알림 전송
     *
     * @param cookieId 쿠키 ID
     * @param webhookUrl Slack Webhook URL
     */
    @Transactional
    public void invalidateAndNotify(Long cookieId, String webhookUrl) {
        Cookie cookie = cookieService.getCookie(cookieId);
        cookie.invalid();

        log.info("[{}] 로그인 풀림.", cookie.getUserName());

        try {
            WebhookResponse webhookResponse = slackService.sendMessage(webhookUrl, "[ " + cookie.getUserName() + " ] 로그인 풀림.");
            log.info("Webhook response code: {}", webhookResponse.getCode());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
