package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrlCallLog;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.schedule.Schedulers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    public String savePointLog(Model model, @PageableDefault(page = 1) Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.

        Page<PointUrlCallLog> list = pointUrlCallLogRepository.findAll(pageable);

        /**
         * blockLimit : page 개수 설정
         * 현재 사용자가 선택한 페이지 앞 뒤로 3페이지씩만 보여준다.
         * ex : 현재 사용자가 4페이지라면 2, 3, (4), 5, 6
         */
        int blockLimit = 10;
        int startPage = (((int) Math.ceil(((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), list.getTotalPages());

        model.addAttribute("items", list);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "point/pointLog";
    }
}
