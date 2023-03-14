package me.synology.hajubal.coins.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "cookie")
public class UserCookieProps {

    private List<String> naver;

    public void setNaver(List<String> naver) {
        this.naver = naver;
    }

    private List<User> users = new ArrayList<>();

    public record User(String name, String cookie) {
    }

    public List<User> getUserCookies() {
        if (users.isEmpty()) {
            users = naver.stream().map(s -> {
                String[] split = s.split(":");
                return new User(split[0], split[1]);
            }).toList();
        }

        return users;
    }
}
