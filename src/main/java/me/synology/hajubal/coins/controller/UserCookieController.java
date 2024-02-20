package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.CookieDto;
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
public class UserCookieController {

    private final UserCookieService userCookieService;

    @GetMapping("/users")
    public String userCookies(Model model) {
        List<UserCookie> list = userCookieService.getAll();
        model.addAttribute("items", list);

        return "users";
    }

    @GetMapping("/user/{userCookieId}")
    public String updateUser(@PathVariable Long userCookieId, Model model) {
        UserCookie userCookie = userCookieService.getUserCookie(userCookieId);
        model.addAttribute("userCookie", userCookie);

        return "editUser";
    }

    @PostMapping("/user/{userId}")
    public String updateUser(@PathVariable Long userId, @Validated @ModelAttribute("userCookie") CookieDto.CookieUpdateDto cookieUpdateDto
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "editUser";
        }

        userCookieService.updateUserCookie(userId, cookieUpdateDto);

        return "redirect:/user/" + userId;
    }

    @GetMapping("/insertUser")
    public String insertUser(Model model) {
        model.addAttribute("userCookie", new CookieDto.CookieInsertDto());

        return "addUser";
    }

    @PostMapping("/insertUser")
    public String insertCookie(@Validated @ModelAttribute("userCookie") CookieDto.CookieInsertDto cookieInsertDto
            , BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "addUser";
        }

        Long userId = userCookieService.insertUserCookie(cookieInsertDto);

        return "redirect:/user/" + userId;
    }

    @DeleteMapping("/user/{userId}")
    public String deleteCookieUser(@PathVariable Long userId) {
        userCookieService.deleteCookieUser(userId);

        return "redirect:/users";
    }
}
