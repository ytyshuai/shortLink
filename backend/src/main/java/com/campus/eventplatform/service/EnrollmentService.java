package com.campus.eventplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.eventplatform.dto.EnrollReq;
import com.campus.eventplatform.entity.Enrollment;

import java.util.List;

public interface EnrollmentService extends IService<Enrollment> {
    Enrollment enroll(EnrollReq req, String username);
    Enrollment checkIn(Long eventId, String username);
    List<Enrollment> getEventEnrollments(Long eventId);
    List<Enrollment> getUserEnrollments(String username);
    void updateEnrollmentStatus(Long id, String status, String adminUsername);
}
