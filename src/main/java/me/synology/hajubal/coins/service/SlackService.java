package me.synology.hajubal.coins.service;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.exception.SlackConfigException;
import me.synology.hajubal.coins.exception.SlackServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * Slack message 전송 서비스
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SlackService {

    private final Slack slack;

    private static final String DEFAULT_NAME = "hgsssss";
    private static final String FALLBACK_DEFAULT = "helloFallback";


    /**
     * 지정된 slack webhook url로 message 전송
     *
     * @param slackWebhookUrl web hook url
     * @param message 전송 메시지
     * @return WebhookResponse 전송 결과
     * @throws SlackServiceException 전송 오류
     * @throws SlackConfigException slack 설정 오류
     */
    @CircuitBreaker(name = DEFAULT_NAME, fallbackMethod = FALLBACK_DEFAULT)
    public WebhookResponse sendMessage(String slackWebhookUrl, String message) throws SlackConfigException, SlackServiceException {
        if(!StringUtils.hasText(slackWebhookUrl)) {
            throw new SlackConfigException("Slack web hook url not set.");
        }

        if(!StringUtils.hasText(message)) {
            throw new SlackServiceException("Slack message is empty.");
        }

        try {
            WebhookResponse webhookResponse = slack.send(slackWebhookUrl, message);

            log.info(webhookResponse.toString());

            return webhookResponse;
        } catch (IOException e) {
            throw new SlackServiceException(e.getMessage(), e);
        }
    }

    private WebhookResponse helloFallback(String name, Throwable t){
        log.error("Fallback : "+ t.getMessage());

        return WebhookResponse.builder().code(500).message("Error for CircuitBreaker").build();
    }

}
