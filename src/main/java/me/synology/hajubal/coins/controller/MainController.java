package me.synology.hajubal.coins.controller;

import me.synology.hajubal.coins.controller.dto.CookieUpdateDto;
import me.synology.hajubal.coins.service.UserCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @Autowired
    private UserCookieService userCookieService;

    @GetMapping("/")
    public String index() {
        return "cookieDetail";
    }

    @PostMapping("/updateCookie")
    public String userCookieUpdate(CookieUpdateDto cookieUpdateDto) {
        userCookieService.updateUserCookie(cookieUpdateDto);

        return "cookieDetail";
    }
}
