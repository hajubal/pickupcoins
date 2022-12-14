package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PointUrlRepositoryImplTest {
    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Test
    void save() {
        PointUrl build = PointUrl.builder().url("url").name("name").build();

        pointUrlRepository.save(build);

        pointUrlRepository.findAll().stream().forEach(System.out::println);

    }
}