package com.campus.eventplatform.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnrollReq {
    @NotNull(message = "活动ID不能为空")
    private Long eventId;
}
