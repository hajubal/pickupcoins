package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.controller.dto.SiteUserDto;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.service.SiteUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SiteUserController {

    private final SiteUserService siteUserService;

    @GetMapping("/siteuser")
    public String getUser(Model model, Authentication authentication) {
        SiteUser siteUser = (SiteUser) authentication.getPrincipal();

        log.info("SiteUser: {}", siteUser);

        siteUser = siteUserService.getSiteUser(siteUser.getLoginId());

        SiteUserDto.UpdateDto updateDto = new SiteUserDto.UpdateDto();
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

}
