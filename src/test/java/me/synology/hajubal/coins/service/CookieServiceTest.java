package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.code.SiteName;
import me.synology.hajubal.coins.controller.dto.UserCookieDto;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.exception.SlackServiceException;
import me.synology.hajubal.coins.respository.CookieRepository;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class CookieServiceTest {

    @Autowired
    private CookieService cookieService;

    @Autowired
    private CookieRepository cookieRepository;

    @MockBean
    private SlackService slackService;

    @Test
    void createUserCookie() {
        //given
        Long userId = createCookie();

        //when
        Optional<Cookie> userCookie = cookieRepository.findById(userId);

        //then
        assertThat(userCookie).isPresent().get()
                .has(new Condition<>(cookie -> cookie.getUserName().equals("test"), "test condition"));
    }

    @Test
    void updateCookieTest() {
        //given
        Long id = createCookie();

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

    @DisplayName("invalid 호출 중 slack 전송시 예외 발생시 invalid 되는지 테스트")
    @Test
    void invalidExceptionTest() {
        //given
        Long cookieId = createCookie();

        given(slackService.sendMessage(any(), any())).willThrow(SlackServiceException.class);

        //when
        cookieService.invalid(cookieId, "");

        //then
        Cookie cookie = cookieService.getCookie(cookieId);
        assertThat(cookie.getIsValid()).isFalse();

    }

    private Long createCookie() {
        UserCookieDto.InsertDto insertDto = new UserCookieDto.InsertDto();
        insertDto.setUserName("test");
        insertDto.setSiteName(SiteName.NAVER.name());
        insertDto.setCookie("cookie");

        return cookieService.insertCookie(insertDto);
    }
}
