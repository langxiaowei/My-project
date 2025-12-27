package com.example.data.controller;

import com.example.data.dto.OverviewDTO;
import com.example.data.dto.R;
import com.example.data.service.OverviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OverviewController {

    private final OverviewService overviewService;

    public OverviewController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/overview")
    public R<OverviewDTO> overview() {
        return R.ok(overviewService.overview());
    }
}