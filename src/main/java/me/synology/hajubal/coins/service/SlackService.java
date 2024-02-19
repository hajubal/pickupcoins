package me.synology.hajubal.coins.service;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.exception.SlackConfigException;
import me.synology.hajubal.coins.exception.SlackServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@Service
public class SlackService {
    /**
     * 지정된 slack webhook url로 message 전송
     *
     * @param message
     * @return WebhookResponse
     * @throws IOException
     */
    public WebhookResponse sendMessage(String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SiteUser principal = (SiteUser) authentication.getPrincipal();

        Slack slack = Slack.getInstance();

        if(principal.getSlackWebhookUrl() == null) {
            throw new SlackConfigException("WebhookUrl not set.");
        }

        Payload payload = Payload.builder().text(message).build();

        WebhookResponse webhookResponse = null;
        try {
            webhookResponse = slack.send(principal.getSlackWebhookUrl(), payload);
        } catch (IOException e) {
            throw new SlackServiceException(e.getMessage(), e);
        }

        log.info(webhookResponse.toString());

        return webhookResponse;
    }
}
