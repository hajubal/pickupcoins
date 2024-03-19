package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.code.SiteName;
import me.synology.hajubal.coins.controller.dto.UserCookieDto;
import me.synology.hajubal.coins.entity.Cookie;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.service.CookieService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CookieController {

    private final CookieService cookieService;

    @ModelAttribute("siteName")
    public List<SiteName> deliveryCodes() {
        return Arrays.stream(SiteName.values()).toList();
    }

    @GetMapping("/cookies")
    public String cookies(Model model) {
        List<Cookie> list = cookieService.getAll();
        model.addAttribute("cookies", list);

        return "cookie/cookies";
    }

    @GetMapping("/cookie/{cookieId}")
    public String updateCookiePage(@PathVariable Long cookieId, Model model) {
        Cookie cookie = cookieService.getCookie(cookieId);
        model.addAttribute("cookie", cookie);

        return "cookie/editCookie";
    }

    @PostMapping("/cookie/{cookieId}")
    public String editCookie(@PathVariable Long cookieId, @Validated @ModelAttribute("cookie") UserCookieDto.UpdateDto editDto
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "cookie/editCookie";
        }

        cookieService.updateCookie(cookieId, editDto);

        return "redirect:/cookie/" + cookieId;
    }

    @GetMapping("/addCookie")
    public String addCookiePage(Model model) {
        model.addAttribute("cookie", new UserCookieDto.InsertDto());

        return "cookie/addCookie";
    }

    @PostMapping("/addCookie")
    public String addCookie(@Validated @ModelAttribute("cookie") UserCookieDto.InsertDto insertDto
            , Authentication authentication, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "cookie/addCookie";
        }

        SiteUser siteUser = (SiteUser) authentication.getPrincipal();

        Long userId = cookieService.insertCookie(insertDto, siteUser.getLoginId());

        return "redirect:/cookie/" + userId;
    }

    @DeleteMapping("/cookie/{cookieId}")
    public String deleteCookie(@PathVariable Long cookieId) {
        cookieService.deleteCookie(cookieId);

        return "redirect:/cookies";
    }
}
