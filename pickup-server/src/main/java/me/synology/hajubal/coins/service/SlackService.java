package me.synology.hajubal.coins.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.exception.SlackConfigException;
import me.synology.hajubal.coins.exception.SlackServiceException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Slack message 전송 서비스
 *
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SlackService {

    private final Slack slack;
    private final ObjectMapper objectMapper;

    /**
     * 지정된 slack webhook url로 message 전송
     *
     * @param slackWebhookUrl web hook url
     * @param message 전송 메시지
     * @return WebhookResponse 전송 결과
     * @throws SlackServiceException 전송 오류
     * @throws SlackConfigException slack 설정 오류
     */
    public WebhookResponse sendMessage(@NotNull String slackWebhookUrl,@NotNull String message) throws SlackConfigException, SlackServiceException {
        if(!StringUtils.hasText(slackWebhookUrl)) {
            throw new SlackConfigException("Slack web hook url not set.");
        }

        if(!StringUtils.hasText(message)) {
            throw new SlackServiceException("Slack message is empty.");
        }

        try {
            Map<String, String> payload = new HashMap<>();
            payload.put("text", message);
            String jsonPayload = objectMapper.writeValueAsString(payload);

            WebhookResponse webhookResponse = slack.send(slackWebhookUrl, jsonPayload);

            log.info(webhookResponse.toString());

            return webhookResponse;
        } catch (IOException e) {
            throw new SlackServiceException(e.getMessage(), e);
        }
    }

}
