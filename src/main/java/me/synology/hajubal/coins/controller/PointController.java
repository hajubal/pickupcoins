package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrlCallLog;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.schedule.Schedulers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Slf4j
@Controller
public class PointController {

    private final PointUrlRepository pointUrlRepository;

    private final PointUrlCallLogRepository pointUrlCallLogRepository;

    private final Schedulers schedulers;

    @GetMapping("/pointUrl")
    public String pointUrl(Model model) {
        model.addAttribute("items", pointUrlRepository.findAll());
        return "point/pointUrl";
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
    public String savePointLog(Model model, @RequestParam(defaultValue = "0") int page) {
        Page<PointUrlCallLog> list = pointUrlCallLogRepository.findAll(PageRequest.of(page, 10));

        model.addAttribute("items", list);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", list.getTotalPages());

        return "point/pointLog";
    }
}
