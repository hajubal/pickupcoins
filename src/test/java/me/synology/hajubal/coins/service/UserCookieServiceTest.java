package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.controller.dto.CookieDto;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserCookieServiceTest {

    @Autowired
    private UserCookieService userCookieService;

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Transactional
    @Test
    void createUserCookie() {
        //given
        CookieDto.CookieInsertDto cookieInsertDto = new CookieDto.CookieInsertDto();
        cookieInsertDto.setUserName("test");
        cookieInsertDto.setSiteName("site");
        cookieInsertDto.setCookie("cookie");

        //when
        Long userId = userCookieService.insertUserCookie(cookieInsertDto);

        //then
        Optional<UserCookie> userCookie = userCookieRepository.findById(userId);

        assertThat(userCookie.isPresent()).isTrue();
    }
}