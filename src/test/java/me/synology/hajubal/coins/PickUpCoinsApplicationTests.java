package me.synology.hajubal.coins;

import me.synology.hajubal.coins.conf.UserCookieProps;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

@SpringBootTest
class PickUpCoinsApplicationTests {

	@Autowired
	private UserCookieProps userCookieProps;


	@Test
	void contextLoads() {
		System.out.println("applicationProps.getUserCookies() = " + userCookieProps.getUserCookies());
	}

	//@RestTemplateTest
	void fileReadTest() throws IOException {
		CharBuffer buffer = CharBuffer.allocate(10000);

		String data = StreamUtils.copyToString(new FileInputStream("C:\\Users\\hajubal\\IdeaProjects\\pickupcoins\\tmp\\cookie.txt"), Charset.defaultCharset());

		System.out.println(">>>" + data);

	}


}
