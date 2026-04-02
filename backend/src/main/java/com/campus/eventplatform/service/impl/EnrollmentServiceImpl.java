package com.campus.eventplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.eventplatform.dto.EnrollReq;
import com.campus.eventplatform.entity.Enrollment;
import com.campus.eventplatform.entity.Event;
import com.campus.eventplatform.entity.User;
import com.campus.eventplatform.mapper.EnrollmentMapper;
import com.campus.eventplatform.service.EnrollmentService;
import com.campus.eventplatform.service.EventService;
import com.campus.eventplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentServiceImpl extends ServiceImpl<EnrollmentMapper, Enrollment> implements EnrollmentService {

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Enrollment enroll(EnrollReq req, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Event event = eventService.getById(req.getEventId());
        if (event == null || "DRAFT".equals(event.getStatus()) || "CANCELLED".equals(event.getStatus())) {
            throw new RuntimeException("活动不可报名");
        }

        if (event.getEndTime() != null && event.getEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("活动已结束");
        }

        // 查重：判断是否已报名
        long count = this.count(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getEventId, req.getEventId())
                .eq(Enrollment::getUserId, user.getId()));
        if (count > 0) {
            throw new RuntimeException("您已报名过该活动，请勿重复报名");
        }

        // 容量限制
        if (event.getCapacity() > 0) {
            long currentEnrollments = this.count(new LambdaQueryWrapper<Enrollment>()
                    .eq(Enrollment::getEventId, req.getEventId())
                    .ne(Enrollment::getStatus, "CANCELLED")
                    .ne(Enrollment::getStatus, "REJECTED"));
            if (currentEnrollments >= event.getCapacity()) {
                throw new RuntimeException("活动名额已满");
            }
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setEventId(event.getId());
        enrollment.setUserId(user.getId());
        enrollment.setStatus("PENDING"); // 默认待审核，可根据业务修改为 APPROVED
        enrollment.setCheckinStatus("UNCHECKED");
        
        this.save(enrollment);
        return enrollment;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Enrollment checkIn(Long eventId, String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        Enrollment enrollment = this.getOne(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getEventId, eventId)
                .eq(Enrollment::getUserId, user.getId()));

        if (enrollment == null) {
            throw new RuntimeException("您未报名该活动");
        }

        if (!"APPROVED".equals(enrollment.getStatus()) && !"PENDING".equals(enrollment.getStatus())) {
            throw new RuntimeException("当前报名状态不允许签到");
        }

        if ("CHECKED".equals(enrollment.getCheckinStatus())) {
            throw new RuntimeException("请勿重复签到");
        }

        enrollment.setCheckinStatus("CHECKED");
        enrollment.setCheckinTime(LocalDateTime.now());
        this.updateById(enrollment);
        
        return enrollment;
    }

    @Override
    public List<Enrollment> getEventEnrollments(Long eventId) {
        return this.list(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getEventId, eventId)
                .orderByDesc(Enrollment::getCreateTime));
    }

    @Override
    public List<Enrollment> getUserEnrollments(String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        return this.list(new LambdaQueryWrapper<Enrollment>()
                .eq(Enrollment::getUserId, user.getId())
                .orderByDesc(Enrollment::getCreateTime));
    }

    @Override
    public void updateEnrollmentStatus(Long id, String status, String adminUsername) {
        User admin = userService.findByUsername(adminUsername);
        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            // 简单校验：非管理员不让审核。也可加入创建者校验逻辑。
            throw new RuntimeException("无权限操作");
        }
        
        Enrollment enrollment = this.getById(id);
        if (enrollment == null) {
            throw new RuntimeException("报名记录不存在");
        }
        
        enrollment.setStatus(status);
        this.updateById(enrollment);
    }
}
