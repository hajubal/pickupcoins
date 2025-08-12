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
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SiteUserController {

    private final SiteUserService siteUserService;

    private final UserDetailsService userDetailsService;

    @GetMapping("/siteUser")
    public String getUser(Model model, Authentication authentication) {
        SiteUser siteUser = (SiteUser) authentication.getPrincipal();

        log.info("SiteUser: {}", siteUser);

        siteUser = siteUserService.getSiteUser(siteUser.getLoginId());

        model.addAttribute("siteUser", SiteUserDto.UpdateDto.fromEntity(siteUser));

        return "siteUser/editUser";
    }

    @PostMapping("/siteUser")
    public String updateUser(@Validated @ModelAttribute("siteUser") SiteUserDto.UpdateDto updateDto
            , Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        log.info("UserDetail: {}", userDetails);

        SiteUser siteUser = siteUserService.getSiteUser(userDetails.getUsername());

        siteUser = siteUserService.updateSiteUser(siteUser.getId(), updateDto);

        log.info("SiteUser: {}", siteUser);

        return "siteUser/editUser";
    }

    @GetMapping("/updatePassword")
    public String editPasswordPage(Model model) {
        model.addAttribute("updatePassword", new PasswordUpdateDto());
        return "siteUser/updatePassword";
    }

    @PostMapping("/updatePassword")
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

        siteUserService.updatePassword(((SiteUser) userDetails).getId(), passwordUpdateDto.getNewPassword());

        return "siteUser/updatePassword";
    }

    @ResponseBody
    @DeleteMapping("/siteUser")
    public String inactivate(Authentication authentication) {

        siteUserService.inActivate(authentication.getName());

        return "ok";
    }
}
