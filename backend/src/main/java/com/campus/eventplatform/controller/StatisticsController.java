package com.campus.eventplatform.controller;

import com.campus.eventplatform.dto.DashboardStats;
import com.campus.eventplatform.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStats> getDashboardStats(Authentication authentication) {
        try {
            String username = authentication.getName();
            DashboardStats stats = statisticsService.getDashboardStats(username);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Failed to get dashboard stats", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
