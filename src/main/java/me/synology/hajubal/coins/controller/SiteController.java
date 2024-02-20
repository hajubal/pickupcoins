package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.Site;
import me.synology.hajubal.coins.respository.SiteRepository;
import me.synology.hajubal.coins.service.SiteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class SiteController {

    private final SiteService siteService;

    @GetMapping("/sites")
    public String sites(Model model) {
        List<Site> siteList = siteService.getAll();
        model.addAttribute("items", siteList);

        return "sites";
    }
}
