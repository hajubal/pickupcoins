package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.UserCookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserCookieRepositoryTest {

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Transactional
    @Test
    void findByUserNameAndSiteName() {


        Optional<UserCookie> cookie = userCookieRepository.findByUserNameAndSiteName("ha", "naver");

        System.out.println("cookie = " + cookie);

    }
}