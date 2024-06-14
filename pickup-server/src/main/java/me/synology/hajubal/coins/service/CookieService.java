package me.synology.hajubal.coins.service;

import com.slack.api.webhook.WebhookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.respository.CookieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 사이트 사용자의 쿠키를 정보를 관리하는 서비스
 */
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
@Service
public class CookieService {

    private final CookieRepository cookieRepository;

    private final SlackService slackService;


    @Transactional
    public void updateCookie(Long cookieId, String cookieStr) {
        log.info("userId: {}, cookie: {}", cookieId, cookieStr);

        Cookie cookie = cookieRepository.findById(cookieId)
                .orElseThrow(() -> new IllegalArgumentException("Not found cookie."));

        cookie.updateCookie(cookieStr);
    }

    public List<Cookie> getAll() {
        return cookieRepository.findAll();
    }

    public List<Cookie> getAll(Long siteUserId) {
        return cookieRepository.findAllBySiteUser_Id(siteUserId);
    }

    public Cookie getCookie(Long cookieId) {
        return cookieRepository.findById(cookieId).orElseThrow(() -> new IllegalArgumentException("Not found cookie."));
    }

    @Transactional
    public void invalid(Long cookieId, String webhookUrl) {
        Cookie cookie = cookieRepository.findById(cookieId).orElseThrow();
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
