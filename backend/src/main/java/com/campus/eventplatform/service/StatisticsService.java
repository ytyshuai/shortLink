package com.campus.eventplatform.service;

import com.campus.eventplatform.dto.DashboardStats;

public interface StatisticsService {
    DashboardStats getDashboardStats(String adminUsername);
    void incrementShortLinkVisit(String shortCode);
    long getShortLinkVisitCount(String shortCode);
}
