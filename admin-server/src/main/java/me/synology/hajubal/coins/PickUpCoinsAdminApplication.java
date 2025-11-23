package me.synology.hajubal.coins;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "me.synology.hajubal.coins")
public class PickUpCoinsAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(PickUpCoinsAdminApplication.class, args);
	}

}
