package me.synology.hajubal.coins.service;

import com.slack.api.Slack;
import com.slack.api.webhook.Payload;
import com.slack.api.webhook.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Properties;

@Slf4j
@Service
public class SlackService {
    private Properties webhookProp;

    public SlackService() {
        webhookProp = new Properties();

        try {
            webhookProp.load(this.getClass().getResourceAsStream("/slackinfo.properties"));
        } catch(Exception e) {
            log.error(e.getMessage());
        }
    }


    /**
     * 지정된 slack webhook url로 message 전송
     *
     * @param message
     * @throws IOException
     */
    public WebhookResponse sendMessage(String message) throws IOException {
        Slack slack = Slack.getInstance();
        //ApiTestResponse response = slack.methods().apiTest(r -> r.foo("bar"));
        //System.out.println(response);

        String webhookUrl = webhookProp.getProperty("webhookurl");

        if(webhookUrl == null) {
            throw new RuntimeException("webhookUrl not set in slackinfo.properties");
        }

        Payload payload = Payload.builder().text(message).build();

        WebhookResponse webhookResponse = slack.send(webhookUrl, payload);

        log.info(webhookResponse.toString());

        return webhookResponse;
    }
}
