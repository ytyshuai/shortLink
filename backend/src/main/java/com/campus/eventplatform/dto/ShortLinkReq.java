package com.campus.eventplatform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShortLinkReq {
    @NotBlank(message = "原始链接不能为空")
    private String originalUrl;
    private Long eventId;
    private LocalDateTime expireTime;
}
