package me.synology.hajubal.coins.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SlackServiceTest {

    @Autowired
    private SlackService slackService;

    @Test
    void sendMessage() throws IOException {
        slackService.sendMessage("test");

    }
}