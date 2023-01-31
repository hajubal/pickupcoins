package me.synology.hajubal.coins.controller;

import jakarta.servlet.http.HttpServletRequest;
import me.synology.hajubal.coins.controller.dto.CookieInsertDto;
import me.synology.hajubal.coins.controller.dto.CookieUpdateDto;
import me.synology.hajubal.coins.entity.*;
import me.synology.hajubal.coins.respository.*;
import me.synology.hajubal.coins.schedule.Schedulers;
import me.synology.hajubal.coins.service.UserCookieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private PointUrlCallLogRepository pointUrlCallLogRepository;

    @Autowired
    private Schedulers schedulers;

    @Autowired
    private SavedPointRepository savedPointRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashBoard(Model model, HttpServletRequest request) {

        List<SavedPoint> all = savedPointRepository.findAll();

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

        return "insertCookie";
    }

    @PostMapping("/insertCookie")
    public String insertCookie(@ModelAttribute CookieInsertDto cookieInsertDto) {

        Long userId = userCookieService.insertUserCookie(cookieInsertDto);

        return "redirect:/updateCookie/" + userId;
    }

    @GetMapping("/crawling")
    public String crawling() {
        schedulers.webCrawlerScheduler();

        return "redirect:/pointurl";
    }

    @GetMapping("/savePoint")
    public String savePoint() {
        schedulers.pointScheduler();

        return "redirect:/pointurl";
    }

    @GetMapping("/savePointLog")
    public String savePointLog(Model model, HttpServletRequest request, @RequestParam(name = "userName", required = false) String userName) {

        List<PointUrlCallLog> list;

        if(StringUtils.hasText(userName)) {
            list = pointUrlCallLogRepository.findByUserName(userName);
        } else {
            list = pointUrlCallLogRepository.findAll();
        }

        model.addAttribute("userName", userName);
        model.addAttribute("items", list);
        model.addAttribute("request", request);

        return "pointLog";
    }
}
