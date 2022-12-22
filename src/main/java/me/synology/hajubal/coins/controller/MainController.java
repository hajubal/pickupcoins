package me.synology.hajubal.coins.controller;

import me.synology.hajubal.coins.controller.dto.CookieUpdateDto;
import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.PointUrlUserCookieRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import me.synology.hajubal.coins.service.UserCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private UserCookieService userCookieService;

    @Autowired
    private UserCookieRepository userCookieRepository;

    @Autowired
    private PointUrlUserCookieRepository pointUrlUserCookieRepository;

    @GetMapping({"/", "/dashboard"})
    public String index(Model model) {

        List<PointUrlUserCookie> all = pointUrlUserCookieRepository.findAll();

        model.addAttribute("items", all);

        return "dashboard";
    }

    @GetMapping("/users")
    public String userCookies(Model model) {
        List<UserCookie> list = userCookieRepository.findAll();

        model.addAttribute("items", list);

        return "users";
    }

    @GetMapping("/updateCookie/{userId}")
    public String updateCookieView(@PathVariable Long userId, Model model) {

        UserCookie userCookie = userCookieRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Not found user."));

        model.addAttribute("userCookie", userCookie);

        return "cookieDetail";
    }

    @PostMapping("/updateCookie")
    public String userCookieUpdate(CookieUpdateDto cookieUpdateDto) {
        userCookieService.updateUserCookie(cookieUpdateDto);

        return "cookieDetail";
    }
}
