package com.campus.eventplatform.dto;

import lombok.Data;

import java.util.List;

@Data
public class DashboardStats {
    private long totalEvents;
    private long totalEnrollments;
    private long totalCheckins;
    private long totalShortLinkVisits;
    private List<EventStat> recentEventStats;

    @Data
    public static class EventStat {
        private Long eventId;
        private String eventTitle;
        private long enrollmentCount;
        private long checkinCount;
        private long visitCount;
    }
}
