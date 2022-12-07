package me.synology.hajubal.coins;

import me.synology.hajubal.coins.service.PointService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileReader;
import java.nio.CharBuffer;

@SpringBootTest
class PickUpCoinsApplicationTests {

	@Autowired
	private PointService pointService;

	@Test
	void contextLoads() {
		pointService.run();
	}

	@Test
	void filereadTest() {
		CharBuffer buffer = CharBuffer.allocate(10000);


		try (FileReader fileReader = new FileReader("C:\\Users\\hajubal\\IdeaProjects\\pickupcoins\\tmp\\cookie.txt")){
			fileReader.read(buffer);

			System.out.println(">>>"+buffer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
