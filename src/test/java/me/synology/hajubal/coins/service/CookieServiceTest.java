package me.synology.hajubal.coins.service;

import jakarta.persistence.EntityManager;
import me.synology.hajubal.coins.code.SiteName;
import me.synology.hajubal.coins.controller.dto.UserCookieDto;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.respository.CookieRepository;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CookieServiceTest {

    @Autowired
    private CookieService cookieService;

    @Autowired
    private CookieRepository cookieRepository;

    @Test
    void createUserCookie() {
        //given
        UserCookieDto.InsertDto insertDto = new UserCookieDto.InsertDto();
        insertDto.setUserName("test");
        insertDto.setSiteName("site");
        insertDto.setCookie("cookie");

        Long userId = cookieService.insertCookie(insertDto);

        //when
        Optional<Cookie> userCookie = cookieRepository.findById(userId);

        //then
        assertThat(userCookie).isPresent().get()
                .has(new Condition<>(cookie -> cookie.getUserName().equals("test"), "test condition"));
    }

    @Test
    void updateCookieTest() {
        //given
        UserCookieDto.InsertDto insertDto = new UserCookieDto.InsertDto();
        insertDto.setUserName("test");
        insertDto.setSiteName(SiteName.NAVER.name());
        insertDto.setCookie("cookie");

        Long id = cookieService.insertCookie(insertDto);

        //when
        UserCookieDto.UpdateDto updateDto = new UserCookieDto.UpdateDto();
        updateDto.setId(id);
        updateDto.setCookie("updateCookie");
        updateDto.setIsValid(Boolean.TRUE);
        updateDto.setUserName("updateUserName");
        updateDto.setSiteName(SiteName.KAKAO.name());

        cookieService.updateCookie(id, updateDto);

        //then
        Cookie cookie = cookieService.getCookie(id);

        assertThat(cookie.getCookie()).isEqualTo(updateDto.getCookie());
        assertThat(cookie.getSiteName()).isEqualTo(updateDto.getSiteName());
        assertThat(cookie.getUserName()).isEqualTo(updateDto.getUserName());
        assertThat(cookie.getIsValid()).isEqualTo(updateDto.getIsValid());
        assertThat(cookie.getSiteName()).isEqualTo(updateDto.getSiteName());

    }
}
