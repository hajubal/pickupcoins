package me.synology.hajubal.coins;

import me.synology.hajubal.coins.service.PointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PickUpCoinsApplicationTests {

	@Autowired
	private PointService pointService;

	@Test
	void contextLoads() {
		pointService.run();
	}


}
