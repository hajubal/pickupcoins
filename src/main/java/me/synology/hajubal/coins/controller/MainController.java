package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import me.synology.hajubal.coins.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MainController {

    private final SavedPointRepository savedPointRepository;

    private final DashboardService dashboardService;

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashBoard(Model model) {
        model.addAttribute("dashboardDto", dashboardService.getDashboard());
        return "dashboard";
    }

    @GetMapping("/demo")
    public String demo() {
        return "index";
    }
}
