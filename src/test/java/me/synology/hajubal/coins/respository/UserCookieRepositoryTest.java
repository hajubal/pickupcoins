package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.UserCookie;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserCookieRepositoryTest {

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Transactional
    @Test
    void findByUserNameAndSiteName() {
        Optional<UserCookie> cookie = userCookieRepository.findByUserNameAndSiteName("ha", "naver");

        Condition<UserCookie> condition = new Condition<>(userCookie -> userCookie.getUserName().equals("ha"), "user cookie user name is ha");

        assertThat(cookie).isPresent().get().has(condition);
    }
}