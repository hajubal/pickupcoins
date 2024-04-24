package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.entity.PointUrl;
import me.synology.hajubal.coins.entity.SavedPoint;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.PointUrlRepository;
import me.synology.hajubal.coins.schedule.Schedulers;
import me.synology.hajubal.coins.service.SavedPointService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class PointController {

    private final PointUrlRepository pointUrlRepository;

    private final SavedPointService savedPointService;

    private final Schedulers schedulers;

    @GetMapping("/pointUrl")
    public String pointUrl(Model model, @PageableDefault(page = 0, size = 10, sort = "createdDate"
            , direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PointUrl> page = pointUrlRepository.findAll(pageable);

        /**
         * blockLimit : page 개수 설정
         * 현재 사용자가 선택한 페이지 앞 뒤로 3페이지씩만 보여준다.
         * ex : 현재 사용자가 4페이지라면 2, 3, (4), 5, 6
         */
        int blockLimit = pageable.getPageSize();
        int startPage = (((int) Math.ceil(((double) (pageable.getPageNumber() + 1) / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), Math.max(page.getTotalPages(), 1));

        log.info("Page number: {}, start page: {}, end page: {}", page.getNumber(), startPage, endPage);

        model.addAttribute("items", page);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

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
    public String savePointLog(Model model, Authentication authentication, @PageableDefault(page = 0, size = 10, sort = "createdDate"
            , direction = Sort.Direction.DESC) Pageable pageable) {

        SiteUser siteUser = (SiteUser) authentication.getPrincipal();

        Page<SavedPoint> page = savedPointService.findAllSavedPoint(siteUser.getId(), pageable);

        int blockLimit = pageable.getPageSize();
        int startPage = (((int) Math.ceil(((double) (pageable.getPageNumber() + 1) / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), Math.max(page.getTotalPages(), 1));

        log.info("Page number: {}, start page: {}, end page: {}", page.getNumber(), startPage, endPage);

        model.addAttribute("items", page);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "point/pointLog";
    }
}
