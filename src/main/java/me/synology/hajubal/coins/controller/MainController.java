package me.synology.hajubal.coins.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
public class MainController {

    @Autowired
    private UserCookieService userCookieService;

    @Autowired
    private UserCookieRepository userCookieRepository;

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
    public String dashBoard(Model model) {

        List<SavedPoint> all = savedPointRepository.findAll();

        model.addAttribute("items", all);

        return "/dashboard";
    }

    @GetMapping("/users")
    public String userCookies(Model model) {
        List<UserCookie> list = userCookieRepository.findAll();

        model.addAttribute("items", list);

        return "/users";
    }

    @GetMapping("/sites")
    public String sites(Model model) {
        List<Site> list = siteRepository.findAll();

        model.addAttribute("items", list);

        return "/sites";
    }

    @GetMapping("/pointurl")
    public String pointurl(Model model) {
        List<PointUrl> list = pointUrlRepository.findAll();

        model.addAttribute("items", list);

        return "/pointurl";
    }

    @GetMapping("/updateCookie/{userId}")
    public String updateCookieView(@PathVariable Long userId, Model model) {

        UserCookie userCookie = userCookieRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Not found user."));

        model.addAttribute("userCookie", userCookie);

        return "/editCookie";
    }

    @PostMapping("/updateCookie/{userId}")
    public String userCookieUpdate(@PathVariable Long userId, @Validated @ModelAttribute("userCookie") CookieUpdateDto cookieUpdateDto
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "/editCookie";
        }

        userCookieService.updateUserCookie(userId, cookieUpdateDto);

        return "redirect:/updateCookie/" + userId;
    }
    
    @GetMapping("/insertCookie")
    public String insertCookieView2(Model model) {
        model.addAttribute("userCookie", new CookieInsertDto());

        return "/insertCookie";
    }

    @PostMapping("/insertCookie")
    public String insertCookie(@Validated @ModelAttribute("userCookie") CookieInsertDto cookieInsertDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "insertCookie";
        }

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
    public String savePointLog(Model model, @RequestParam(name = "userName", required = false) String userName) {

        List<PointUrlCallLog> list;

        if (StringUtils.hasText(userName)) {
            list = pointUrlCallLogRepository.findByUserName(userName);
        } else {
            list = pointUrlCallLogRepository.findAll();
        }

        model.addAttribute("userName", userName);
        model.addAttribute("items", list);

        return "pointLog";
    }
}
