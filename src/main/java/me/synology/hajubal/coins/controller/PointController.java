package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.PointUrlCallLog;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.schedule.Schedulers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class PointController {

    private final PointUrlRepository pointUrlRepository;

    private final PointUrlCallLogRepository pointUrlCallLogRepository;

    private final Schedulers schedulers;

    @GetMapping("/pointUrl")
    public String pointUrl(Model model) {
        List<PointUrl> list = pointUrlRepository.findAll();

        model.addAttribute("items", list);

        return "pointUrl";
    }

    @GetMapping("/crawling")
    public String crawling() {
        schedulers.webCrawlerScheduler();

        return "redirect:/pointUrl";
    }

    @GetMapping("/savePoint")
    public String savePoint() {
        schedulers.pointScheduler();

        return "redirect:/pointUrl";
    }

    @GetMapping("/savePointLog")
    public String savePointLog(Model model, @RequestParam(name = "userName", defaultValue = "ha") String[] userName) {

        List<PointUrlCallLog> list;

        if (userName.length == 0) {
            list = pointUrlCallLogRepository.findAll();
        } else {
            list = pointUrlCallLogRepository.findByUserNameIn(userName);
        }

        model.addAttribute("userName", userName);
        model.addAttribute("items", list);

        return "pointLog";
    }
}
