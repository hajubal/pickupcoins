package me.synology.hajubal.coins;

import me.synology.hajubal.coins.pages.HomePage;
import me.synology.hajubal.coins.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@Disabled
//FIXME test profile 사용시 오류 발생
@ActiveProfiles("local")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PickUpCoinsLoginTest {

    private WebDriver driver;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        this.driver = new HtmlUnitDriver();
    }

    @AfterEach
    void tearDown() {
        this.driver.quit();
    }

    @Test
    void login() {
        final LoginPage loginPage = HomePage.to(this.driver, this.port);
        loginPage.assertAt();

        HomePage homePage = loginPage.loginForm().username("user").password("user").submit();
        homePage.assertAt();

//        LoginPage logoutSuccess = homePage.logout();
//        logoutSuccess.assertAt();
    }

}