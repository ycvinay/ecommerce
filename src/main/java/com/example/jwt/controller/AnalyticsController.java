package com.example.jwt.controller;

import com.example.jwt.dto.AnalyticsSummaryDTO;
import com.example.jwt.service.AnalyticsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:3000")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping()
    public AnalyticsSummaryDTO getAnalyticsSummary() {
        return analyticsService.getAnalyticsSummary();
    }
}
