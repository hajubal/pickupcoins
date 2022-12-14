package me.synology.hajubal.coins.controller;

import me.synology.hajubal.coins.controller.dto.CookieUpdateDto;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import me.synology.hajubal.coins.service.UserCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private UserCookieService userCookieService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/updateCookie")
    public void userCookieUpdate(CookieUpdateDto cookieUpdateDto) {
        userCookieService.updateUserCookie(cookieUpdateDto);
    }
}
