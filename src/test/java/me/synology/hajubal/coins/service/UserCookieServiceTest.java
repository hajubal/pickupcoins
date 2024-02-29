package me.synology.hajubal.coins.service;

import me.synology.hajubal.coins.controller.dto.UserCookieDto;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import org.assertj.core.api.Condition;
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
        UserCookieDto.InsertDto insertDto = new UserCookieDto.InsertDto();
        insertDto.setUserName("test");
        insertDto.setSiteName("site");
        insertDto.setCookie("cookie");

        Long userId = userCookieService.addUserCookie(insertDto);

        //when
        Optional<UserCookie> userCookie = userCookieRepository.findById(userId);

        //then
        assertThat(userCookie).isPresent().get()
                .has(new Condition<>(cookie -> cookie.getUserName().equals("test"), "test condition"));
    }
}
