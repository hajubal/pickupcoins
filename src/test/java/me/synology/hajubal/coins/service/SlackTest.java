package me.synology.hajubal.coins.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
public class SlackTest {

    @Autowired
    private SlackService slackService;

    @Disabled
    @DisplayName("api url 정상동작 테스트. 자신의 url 입력하여 테스트")
    @Test
    void slackUrlTest() throws Exception {

        slackService.sendMessage("https://hooks.slack.com/services/~~", "{\"text\":\"Hello, World!\"}");
    }
}
