package me.synology.hajubal.coins;

import jakarta.annotation.PostConstruct;
import me.synology.hajubal.coins.entity.CookieData;
import me.synology.hajubal.coins.respository.CookieRepository;
import me.synology.hajubal.coins.service.NaverPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PickUpCoinsApplication {

	@Autowired
	private NaverPointService naverPointService;

	@Autowired
	private CookieRepository cookieRepository;

	public static void main(String[] args) {
		SpringApplication.run(PickUpCoinsApplication.class, args);
	}

	@PostConstruct
	public void init() {
		String COOKIE = "";

		CookieData cookieData = CookieData.builder().id(1l).cookie(COOKIE).build();

		cookieRepository.save(cookieData);

		naverPointService.savePoint();
	}
}
