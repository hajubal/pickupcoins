package me.synology.hajubal.coins.respository;

import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.type.POINT_URL_TYPE;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@Import(TestJpaConfig.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PointUrlRepositoryImplTest {
    @Autowired
    private PointUrlRepository pointUrlRepository;

    @Transactional
    @DisplayName("저장 테스트")
    @Test
    void saveTest() {
        PointUrl pointUrl = PointUrl.builder().url("url").build();

        pointUrlRepository.save(pointUrl);

        Optional<PointUrl> byId = pointUrlRepository.findById(pointUrl.getId());

        assertThat(byId).contains(pointUrl);
    }

    @Transactional
    @DisplayName("호출안된 url 조회 테스트")
    @Test
    void findByNotCalledUrlQueryTest() {
        PointUrl pointUrl = PointUrl.builder().url("url").build();

        pointUrlRepository.save(pointUrl);

        List<PointUrl> urls = pointUrlRepository.findByNotCalledUrl(POINT_URL_TYPE.UNSUPPORT.name(), "ha");

        assertThat(urls).hasSize(1);
    }
}