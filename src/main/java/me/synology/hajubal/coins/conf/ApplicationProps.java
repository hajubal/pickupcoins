package me.synology.hajubal.coins.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//@Component
//@ConfigurationProperties(prefix = "application")
public class ApplicationProps {

    private List<Map<String, Object>> props;
    private List<User> users;

    // getters and setters

    public static class User {

        private String username;
        private String password;
        private List<String> roles;

        // getters and setters

    }
}
