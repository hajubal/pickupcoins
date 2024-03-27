package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.respository.SavedPointRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class MainController {

    private final SavedPointRepository savedPointRepository;

    @GetMapping("/")
    public String index() {
        return "redirect:dashboard";
    }

    @GetMapping("/dashboard")
    public String dashBoard(Model model) {

        List<SavedPoint> all = savedPointRepository.findAll();

        model.addAttribute("items", all);

        return "dashboard";
    }

    @GetMapping("/demo")
    public String demo() {
        return "index";
    }
}
