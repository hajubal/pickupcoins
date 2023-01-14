package me.synology.hajubal.coins.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.synology.hajubal.coins.controller.dto.CookieInsertDto;
import me.synology.hajubal.coins.controller.dto.CookieUpdateDto;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlUserCookie;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.respository.PointUrlUserCookieRepository;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.respository.UserCookieRepository;
import me.synology.hajubal.coins.service.UserCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private PointUrlRepository pointUrlRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashBoard(Model model, HttpServletRequest request) {

        List<PointUrlUserCookie> all = pointUrlUserCookieRepository.findAll();

        model.addAttribute("items", all);
        model.addAttribute("request", request);

        return "dashboard";
    }

    @GetMapping("/users")
    public String userCookies(Model model, HttpServletRequest request) {
        List<UserCookie> list = userCookieRepository.findAll();

        model.addAttribute("items", list);
        model.addAttribute("request", request);

        return "users";
    }

    @GetMapping("/sites")
    public String sites(Model model, HttpServletRequest request) {
        List<Site> list = siteRepository.findAll();

        model.addAttribute("items", list);
        model.addAttribute("request", request);

        return "sites";
    }

    @GetMapping("/pointurl")
    public String pointurl(Model model, HttpServletRequest request) {
        List<PointUrl> list = pointUrlRepository.findAll();

        model.addAttribute("items", list);
        model.addAttribute("request", request);

        return "pointurl";
    }

    @GetMapping("/updateCookie/{userId}")
    public String updateCookieView(@PathVariable Long userId, Model model, HttpServletRequest request) {

        UserCookie userCookie = userCookieRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Not found user."));

        model.addAttribute("userCookie", userCookie);
        model.addAttribute("request", request);

        return "cookieDetail";
    }

    @PostMapping("/updateCookie/{userId}")
    public String userCookieUpdate(@PathVariable Long userId, CookieUpdateDto cookieUpdateDto) {
        userCookieService.updateUserCookie(userId, cookieUpdateDto);

        return "redirect:/updateCookie/" + userId;
    }

    @GetMapping("/insertCookie")
    public String insertCookieView(Model model, HttpServletRequest request) {
        model.addAttribute("request", request);
        model.addAttribute("userCookie", new CookieInsertDto());

        return "inserCookie";
    }

    @PostMapping("/insertCookie")
    public String insertCookie(@ModelAttribute CookieInsertDto cookieInsertDto) {

        Long userId = userCookieService.insertUserCookie(cookieInsertDto);

        return "redirect:/updateCookie/" + userId;
    }

    @GetMapping("/crawlering")
    public String crawling() {
        return "";
    }

}
