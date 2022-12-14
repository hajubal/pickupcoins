package me.synology.hajubal.coins.service;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@Service
public class SlackService {

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    /**
     * 지정된 slack webhook url로 message 전송
     *
     * @param message
     * @throws IOException
     */
    public WebhookResponse sendMessage(String message) {
        Slack slack = Slack.getInstance();
        //ApiTestResponse response = slack.methods().apiTest(r -> r.foo("bar"));
        //System.out.println(response);

        if(webhookUrl == null) {
            throw new RuntimeException("webhookUrl not set.");
        }

        Payload payload = Payload.builder().text(message).build();

        WebhookResponse webhookResponse = null;
        try {
            webhookResponse = slack.send(webhookUrl, payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.info(webhookResponse.toString());

        return webhookResponse;
    }
}
