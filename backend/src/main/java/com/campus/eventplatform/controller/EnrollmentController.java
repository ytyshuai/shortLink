package com.campus.eventplatform.controller;

import com.campus.eventplatform.common.Result;
import com.campus.eventplatform.dto.EnrollReq;
import com.campus.eventplatform.entity.Enrollment;
import com.campus.eventplatform.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @PostMapping
    public Result<Enrollment> enroll(@Valid @RequestBody EnrollReq req, Authentication authentication) {
        try {
            Enrollment enrollment = enrollmentService.enroll(req, authentication.getName());
            return Result.success(enrollment);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @PostMapping("/checkin/{eventId}")
    public Result<Enrollment> checkIn(@PathVariable Long eventId, Authentication authentication) {
        try {
            Enrollment enrollment = enrollmentService.checkIn(eventId, authentication.getName());
            return Result.success(enrollment);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/event/{eventId}")
    public Result<List<Enrollment>> getEventEnrollments(@PathVariable Long eventId) {
        return Result.success(enrollmentService.getEventEnrollments(eventId));
    }

    @GetMapping("/user")
    public Result<List<Enrollment>> getUserEnrollments(Authentication authentication) {
        return Result.success(enrollmentService.getUserEnrollments(authentication.getName()));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication authentication) {
        try {
            String status = body.get("status");
            enrollmentService.updateEnrollmentStatus(id, status, authentication.getName());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}
