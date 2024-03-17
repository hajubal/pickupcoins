package me.synology.hajubal.coins.service;

import com.slack.api.Slack;
import com.slack.api.webhook.WebhookResponse;
import me.synology.hajubal.coins.exception.SlackConfigException;
import me.synology.hajubal.coins.exception.SlackServiceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class SlackServiceTest {

    @MockBean
    private Slack slack;

    @Mock
    private WebhookResponse webhookResponse;

    @Autowired
    private SlackService slackService;



    @DisplayName("slack send 테스트")
    @Test
    void sendMessage() throws Exception {

        //given
        given(slack.send(anyString(), anyString())).willReturn(webhookResponse);
        given(webhookResponse.getCode()).willReturn(200);

        //when
        WebhookResponse response = slackService.sendMessage("http://url", "message");

        //then
        assertThat(response.getCode()).isEqualTo(200);
    }

    @DisplayName("WebhookUrl 이 Null인 경우 예외 테스트")
    @Test
    void urlNullTest() {
        assertThatThrownBy(() -> slackService.sendMessage(null, "message"))
                .isInstanceOf(SlackConfigException.class);
    }

    @DisplayName("Message가 Null인 경우 예외 테스트")
    @Test
    void messageNullTest() {
        assertThatThrownBy(() -> slackService.sendMessage("url", null))
                .isInstanceOf(SlackServiceException.class)
                .hasMessage("Slack message is empty.");
    }
}