package com.campus.eventplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.eventplatform.dto.DashboardStats;
import com.campus.eventplatform.entity.Enrollment;
import com.campus.eventplatform.entity.Event;
import com.campus.eventplatform.entity.ShortLink;
import com.campus.eventplatform.entity.User;
import com.campus.eventplatform.mapper.EnrollmentMapper;
import com.campus.eventplatform.mapper.EventMapper;
import com.campus.eventplatform.mapper.ShortLinkMapper;
import com.campus.eventplatform.mapper.UserMapper;
import com.campus.eventplatform.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticsServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment> implements StatisticsService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private EventMapper eventMapper;
    
    @Autowired
    private ShortLinkMapper shortLinkMapper;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public DashboardStats getDashboardStats(String adminUsername) {
        DashboardStats stats = new DashboardStats();
        
        // 1. 检查管理员权限
        User admin = userMapper.selectByUsername(adminUsername);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            throw new RuntimeException("无权限访问统计数据");
        }
        
        // 2. 获取总活动数
        long totalEvents = eventMapper.selectCount(new LambdaQueryWrapper<Event>()
                .ne(Event::getStatus, "DRAFT")
                .eq(Event::getDeleted, 0));
        stats.setTotalEvents(totalEvents);
        
        // 3. 获取总报名数
        long totalEnrollments = enrollmentMapper.selectCount(new LambdaQueryWrapper<Enrollment>()
                .ne(Enrollment::getStatus, "CANCELLED")
                .eq(Enrollment::getDeleted, 0));
        stats.setTotalEnrollments(totalEnrollments);
        
        // 4. 获取总签到数
        long totalCheckins = enrollmentMapper.selectCount(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getCheckinStatus, "CHECKED")
                .eq(Enrollment::getDeleted, 0));
        stats.setTotalCheckins(totalCheckins);
        
        // 5. 获取短链总访问量
        totalShortLinkVisits = getShortLinkTotalVisits();
        stats.setTotalShortLinkVisits(totalShortLinkVisits);
        
        // 6. 获取近期活动统计（最近30天创建的活动）
        List<Event> recentEvents = eventMapper.selectList(new LambdaQueryWrapper<Event>()
                .ge(Event::getCreateTime, java.time.LocalDateTime.now().minusDays(30))
                .eq(Event::getDeleted, 0));
        
        List<DashboardStats.EventStat> eventStats = new ArrayList<>();
        for (Event event : recentEvents) {
            DashboardStats.EventStat eventStat = new DashboardStats.EventStat();
            eventStat.setEventId(event.getId());
            eventStat.setEventTitle(event.getTitle());
            
            // 获取该活动的报名数
            long enrollments = enrollmentMapper.selectCount(new LambdaQueryWrapper<Enrollment>()
                    .eq(Enrollment::getEventId, event.getId())
                    .ne(Enrollment::getStatus, "CANCELLED")
                    .eq(Enrollment::getDeleted, 0));
            eventStat.setEnrollmentCount(enrollments);
            
            // 获取该活动的签到数
            long checkins = enrollmentMapper.selectCount(new LambdaQueryWrapper<Enrollment>()
                    .eq(Enrollment::getEventId, event.getId())
                    .eq(Enrollment::getCheckinStatus, "CHECKED")
                    .eq(Enrollment::getDeleted, 0));
            eventStat.setCheckinCount(checkins);
            
            // 获取该活动的短链访问量
            long visits = getEventShortLinkVisits(event.getId());
            eventStat.setVisitCount(visits);
            
            eventStats.add(eventStat);
        }
        
        stats.setRecentEventStats(eventStats);
        
        return stats;
    }

    @Override
    public void incrementShortLinkVisit(String shortCode) {
        try {
            String key = "short_link:visits:" + shortCode;
            redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            log.error("Failed to increment short link visit count: {}", shortCode, e);
        }
    }

    @Override
    public long getShortLinkVisitCount(String shortCode) {
        try {
            String key = "short_link:visits:" + shortCode;
            String count = redisTemplate.opsForValue().get(key);
            return count == null ? 0 : Long.parseLong(count);
        } catch (Exception e) {
            log.error("Failed to get short link visit count: {}", shortCode, e);
            return 0;
        }
    }

    private long getShortLinkTotalVisits() {
        try {
            // 获取所有短链的访问量
            List<ShortLink> shortLinks = shortLinkMapper.selectList(new LambdaQueryWrapper<ShortLink>()
                    .eq(ShortLink::getDeleted, 0));
            
            long total = 0;
            for (ShortLink shortLink : shortLinks) {
                total += getShortLinkVisitCount(shortLink.getShortCode());
            }
            return total;
        } catch (Exception e) {
            log.error("Failed to get total short link visits", e);
            return 0;
        }
    }

    private long getEventShortLinkVisits(Long eventId) {
        try {
            // 获取该活动的所有短链
            List<ShortLink> shortLinks = shortLinkMapper.selectList(new LambdaQueryWrapper<ShortLink>()
                    .eq(ShortLink::getEventId, eventId)
                    .eq(ShortLink::getDeleted, 0));
            
            long total = 0;
            for (ShortLink shortLink : shortLinks) {
                total += getShortLinkVisitCount(shortLink.getShortCode());
            }
            return total;
        } catch (Exception e) {
            log.error("Failed to get event short link visits for event: {}", eventId, e);
            return 0;
        }
    }
}
