package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.PasswordUpdateDto;
import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.service.SiteUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SiteUserController {

    private final SiteUserService siteUserService;

    private final UserDetailsService userDetailsService;

    @GetMapping("/siteuser")
    public String getUser(Model model, Authentication authentication) {
        SiteUser siteUser = (SiteUser) authentication.getPrincipal();

        log.info("SiteUser: {}", siteUser);

        siteUser = siteUserService.getSiteUser(siteUser.getLoginId());

        SiteUserDto.UpdateDto updateDto = new SiteUserDto.UpdateDto();
        updateDto.setId(siteUser.getId());
        updateDto.setLoginId(siteUser.getLoginId());
        updateDto.setUserName(siteUser.getUserName());
        updateDto.setSlackWebhookUrl(siteUser.getSlackWebhookUrl());

        model.addAttribute("siteUser", updateDto);

        return "/siteUser/editUser";
    }

    @PostMapping("/siteuser")
    public String updateUser(@Validated @ModelAttribute("siteUser") SiteUserDto.UpdateDto updateDto
            , Authentication authentication) {
        SiteUser siteUser = (SiteUser) authentication.getPrincipal();

        siteUser = siteUserService.getSiteUser(siteUser.getLoginId());

        siteUser = siteUserService.updateSiteUser(siteUser.getId(), updateDto);

        log.info("SiteUser: {}", siteUser);

        return "/siteUser/editUser";
    }

    @GetMapping("/editPassword")
    public String editPasswordPage(Model model) {
        model.addAttribute("updatePassword", new PasswordUpdateDto());
        return "/siteUser/editUserPassword";
    }

    @PostMapping("/editPassword")
    public String updatePassword(Authentication authentication
            , @Validated @ModelAttribute("updatePassword") PasswordUpdateDto passwordUpdateDto
            , BindingResult bindingResult) {

        log.info("Update password authentication: {}", authentication);

        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());

        log.info("UserDetail: {}", userDetails);

        if(!passwordUpdateDto.getNewPassword().equals(passwordUpdateDto.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "editPassword.error.notConfirm");
        }

        PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        boolean matches = passwordEncoder.matches(passwordUpdateDto.getPassword(), userDetails.getPassword());

        if(!matches) {
            //global message setting
//            bindingResult.addError(new ObjectError("updatePassword.password", "기존 비밀번호와 일치 하지 않는다"));
            bindingResult.rejectValue("password", "editPassword.error.password");
        }

        siteUserService.updatePassword(((SiteUser) userDetails).getId(), passwordUpdateDto.getPassword());

        return "/siteUser/editUserPassword";
    }
}
