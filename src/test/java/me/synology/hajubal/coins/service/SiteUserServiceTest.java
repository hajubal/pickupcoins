package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SiteUserServiceTest {

    @Autowired
    private SiteUserService siteUserService;

    @Autowired
    private SiteUserRepository siteUserRepository;

    @Transactional
    @Test
    void updateSiteUserTest() {
        String loginId = System.currentTimeMillis() + "";

        //given
        SiteUser beforeSiteUser = siteUserRepository.save(SiteUser.builder()
                .loginId(loginId).password("password").userName("userName").slackWebhookUrl("slackUrl")
                .build());

        //when
        SiteUserDto.UpdateDto updateDto = new SiteUserDto.UpdateDto();
        updateDto.setUserName("updateUserName");
        updateDto.setSlackWebhookUrl("updateSlackUrl");

        siteUserService.updateSiteUser(beforeSiteUser.getId(), updateDto);

        //then
        SiteUser updateSiteUser = siteUserRepository.findById(beforeSiteUser.getId()).orElseThrow();

        assertThat(updateSiteUser.getUserName()).isEqualTo(updateDto.getUserName());
        assertThat(updateSiteUser.getSlackWebhookUrl()).isEqualTo(updateDto.getSlackWebhookUrl());

    }

    @Transactional
    @Test
    void getSiteUserTest() {
        String loginId = System.currentTimeMillis() + "";

        //given
        SiteUser beforeSiteUser = siteUserRepository.save(SiteUser.builder()
                .loginId(loginId).password("password").userName("userName").slackWebhookUrl("slackUrl")
                .build());

        //when


        //then
        SiteUser updateSiteUser = siteUserRepository.findById(beforeSiteUser.getId()).orElseThrow();

        assertThat(updateSiteUser).isEqualTo(beforeSiteUser);

    }

    @Transactional
    @Test
    void updatePasswordTest() {
        String loginId = System.currentTimeMillis() + "";

        //given
        SiteUser beforeSiteUser = siteUserRepository.save(SiteUser.builder()
                .loginId(loginId).password("password").userName("userName").slackWebhookUrl("slackUrl")
                .build());

        //when
        siteUserService.updatePassword(beforeSiteUser.getId(), "newPassword");

        //then
        SiteUser updateSiteUser = siteUserRepository.findById(beforeSiteUser.getId()).orElseThrow();

        assertThat(PasswordEncoderFactories.createDelegatingPasswordEncoder().matches("newPassword", updateSiteUser.getPassword())).isTrue();
    }
}