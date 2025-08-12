package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.code.SiteName;
import me.synology.hajubal.coins.controller.dto.UserCookieDto;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.respository.*;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Autowired
    private PointUrlCookieRepository pointUrlCookieRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Autowired
    private SavedPointRepository savedPointRepository;

    @Autowired
    private SiteUserRepository siteUserRepository;

    @BeforeEach
    void setup() {
        cookieRepository.deleteAll();
        siteUserRepository.deleteAll();
    }

    @DisplayName("cookie 생성 테스트")
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

    @DisplayName("cookie 정보 수정 테스트")
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

    @DisplayName("cookie 삭제 테스트")
    @Test
    void deleteTest() throws Exception {
        //given
        //insert cookie, pointUrlCookie, savedPoint
        Long cookieId = createCookie();
        Cookie cookie = cookieService.getCookie(cookieId);

        PointUrl pointUrl = PointUrl.builder().url("url").build();
        pointUrlRepository.save(pointUrl);

        PointUrlCookie pointUrlCookie = PointUrlCookie.builder().pointUrl(pointUrl).cookie(cookie).build();
        pointUrlCookieRepository.save(pointUrlCookie);

        SavedPoint savedPoint = SavedPoint.builder().amount(10).responseBody("body").cookie(cookie).build();
        savedPointRepository.save(savedPoint);

        //when
        cookieService.deleteCookie(cookieId);

        //then
        assertThat(pointUrlCookieRepository.findById(pointUrlCookie.getId()).isEmpty()).isTrue();
        assertThat(savedPointRepository.findById(savedPoint.getId()).isEmpty()).isTrue();
    }


    /**
     * cookie 생성 핼퍼 함수
     */
    private Long createCookie() {
        SiteUser siteUser = SiteUser.builder().userName("name").loginId("loginId").slackWebhookUrl("url").password("pw").build();
        siteUserRepository.save(siteUser);

        UserCookieDto.InsertDto insertDto = new UserCookieDto.InsertDto();
        insertDto.setUserName("test");
        insertDto.setSiteName(SiteName.NAVER.name());
        insertDto.setCookie("cookie");

        return cookieService.insertCookie(insertDto, siteUser.getLoginId());
    }
}
