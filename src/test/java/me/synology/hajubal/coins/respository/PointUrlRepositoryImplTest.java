package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrlData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PointUrlRepositoryImplTest {
    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Test
    void save() {
        PointUrlData build = PointUrlData.builder().url("url").name("name").build();

        pointUrlRepository.save(build);

        pointUrlRepository.findAll().stream().forEach(System.out::println);

    }
}