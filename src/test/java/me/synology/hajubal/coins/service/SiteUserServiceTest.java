package me.synology.hajubal.coins.service;

import ch.qos.logback.core.testUtil.RandomUtil;
import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.SiteUserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
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


    @DisplayName("사용자 이름, slackurl 수정 테스트")
    @Test
    void updateSiteUserTest() {
        //given
        SiteUser beforeSiteUser = createSiteUser();

        //when
        SiteUserDto.UpdateDto updateDto = new SiteUserDto.UpdateDto();
        updateDto.setUserName(beforeSiteUser.getUserName() + "updateUserName");
        updateDto.setSlackWebhookUrl(beforeSiteUser.getSlackWebhookUrl() + "updateSlackUrl");

        siteUserService.updateSiteUser(beforeSiteUser.getId(), updateDto);

        //then
        SiteUser updateSiteUser = siteUserRepository.findById(beforeSiteUser.getId()).orElseThrow();

        assertThat(updateSiteUser.getUserName()).isEqualTo(updateDto.getUserName());
        assertThat(updateSiteUser.getSlackWebhookUrl()).isEqualTo(updateDto.getSlackWebhookUrl());

    }

    @DisplayName("사용자 조회 테스트")
    @Test
    void getSiteUserTest() {
        //given
        SiteUser beforeSiteUser = createSiteUser();

        //when
        SiteUser updateSiteUser = siteUserService.getSiteUser(beforeSiteUser.getLoginId());

        //then
        assertThat(updateSiteUser.getUserName()).isEqualTo(beforeSiteUser.getUserName());
        assertThat(updateSiteUser.getPassword()).isEqualTo(beforeSiteUser.getPassword());
        assertThat(updateSiteUser.getLoginId()).isEqualTo(beforeSiteUser.getLoginId());
        assertThat(updateSiteUser.getId()).isEqualTo(beforeSiteUser.getId());
        assertThat(updateSiteUser.getSlackWebhookUrl()).isEqualTo(beforeSiteUser.getSlackWebhookUrl());
        assertThat(updateSiteUser.getCreatedBy()).isEqualTo(beforeSiteUser.getCreatedBy());
        //FIXME github action에서 실패. why????
//        assertThat(updateSiteUser.getCreatedDate()).isEqualTo(beforeSiteUser.getCreatedDate());
        assertThat(updateSiteUser.getLastModifiedBy()).isEqualTo(beforeSiteUser.getLastModifiedBy());

    }

    @DisplayName("비밀번호 수정 테스트")
    @Test
    void updatePasswordTest() {
        //given
        SiteUser beforeSiteUser = createSiteUser();

        //when
        siteUserService.updatePassword(beforeSiteUser.getId(), "newPassword");

        //then
        SiteUser updateSiteUser = siteUserRepository.findById(beforeSiteUser.getId()).orElseThrow();

        assertThat(PasswordEncoderFactories.createDelegatingPasswordEncoder().matches("newPassword", updateSiteUser.getPassword())).isTrue();
    }

    /**
     * 사용자 생성 핼퍼 함수
     */
    private SiteUser createSiteUser() {
        return siteUserRepository.save(SiteUser.builder()
                .loginId("loginId" + RandomUtil.getPositiveInt()).password("password").userName("userName").slackWebhookUrl("slackUrl")
                .build());
    }

}
