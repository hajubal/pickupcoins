package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.UserCookieDto;
import me.synology.hajubal.coins.entity.UserCookie;
import me.synology.hajubal.coins.service.UserCookieService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class CookieController {

    private final UserCookieService userCookieService;

    @GetMapping("/cookies")
    public String cookies(Model model) {
        List<UserCookie> list = userCookieService.getAll();
        model.addAttribute("cookies", list);

        return "cookie/cookies";
    }

    @GetMapping("/cookie/{cookieId}")
    public String updateCookiePage(@PathVariable Long cookieId, Model model) {
        UserCookie cookie = userCookieService.getUserCookie(cookieId);
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

        userCookieService.updateUserCookie(cookieId, editDto);

        return "redirect:/cookie/" + cookieId;
    }

    @GetMapping("/addCookie")
    public String addCookiePage(Model model) {
        model.addAttribute("cookie", new UserCookieDto.InsertDto());

        return "cookie/addCookie";
    }

    @PostMapping("/addCookie")
    public String addCookie(@Validated @ModelAttribute("cookie") UserCookieDto.InsertDto insertDto
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "cookie/addCookie";
        }

        Long userId = userCookieService.addUserCookie(insertDto);

        return "redirect:/cookie/" + userId;
    }

    @DeleteMapping("/cookie/{cookieId}")
    public String deleteCookie(@PathVariable Long cookieId) {
        userCookieService.deleteCookieUser(cookieId);

        return "redirect:/cookies";
    }
}
