package me.synology.hajubal.coins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PickUpCoinsApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickUpCoinsApplication.class, args);
	}

}
