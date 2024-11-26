package me.synology.hajubal.coins.conf;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class CommonControllerAdvice {
    private final BuildProperties buildProperties;

    @Value("${spring.profiles.active:default}")
    private String profile;

    @ModelAttribute("version")
    String getVersion() {
        return buildProperties.getVersion();
    }

    @ModelAttribute("activeProfile")
    String getActiveProfile() {
        return profile;
    }

}
