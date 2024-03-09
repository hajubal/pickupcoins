package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrlCallLog;
import me.synology.hajubal.coins.respository.PointUrlCallLogRepository;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.schedule.Schedulers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String savePointLog(Model model, @PageableDefault(page = 0, size = 10, sort = "createdDate"
            , direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PointUrlCallLog> list = pointUrlCallLogRepository.findAll(pageable);

        /**
         * blockLimit : page 개수 설정
         * 현재 사용자가 선택한 페이지 앞 뒤로 3페이지씩만 보여준다.
         * ex : 현재 사용자가 4페이지라면 2, 3, (4), 5, 6
         */
        int blockLimit = pageable.getPageSize();
        int startPage = (((int) Math.ceil(((double) (pageable.getPageNumber() + 1) / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), list.getTotalPages());

        log.info("Page number: {}", list.getNumber());
        log.info("Start page: {}", startPage);
        log.info("End page: {}", endPage);

        model.addAttribute("items", list);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "point/pointLog";
    }
}
