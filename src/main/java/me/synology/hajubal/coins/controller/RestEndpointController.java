package me.synology.hajubal.coins.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.synology.hajubal.coins.service.ReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j
@RestController
public class RestEndpointController {

    private final ReportService reportService;

    @GetMapping("/report")
    public String report() {
        reportService.report();

        return "ok";
    }
}
