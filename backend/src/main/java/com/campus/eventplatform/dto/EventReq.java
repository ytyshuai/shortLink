package com.campus.eventplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EventReq {
    @NotBlank(message = "活动标题不能为空")
    private String title;
    private String description;
    @NotBlank(message = "活动地点不能为空")
    private String location;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
    private Integer capacity = 0;
    private String status = "DRAFT"; // DRAFT, PUBLISHED
}
