package me.synology.hajubal.coins.admin.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "pickup-server")
public class PickupServerProperties {
    private String baseUrl = "http://localhost:7070";
}
