package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.entity.UserCookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SavedPointRepositoryTest {

    @Autowired
    private SavedPointRepository savedPointRepository;

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Test
    void allTest() {

        List<SavedPoint> all = savedPointRepository.findAll();

        all.forEach(System.out::println);
    }

    @Test
    void userCookieTest() {
        List<UserCookie> all = userCookieRepository.findAll();

        all.forEach(System.out::println);
    }
}