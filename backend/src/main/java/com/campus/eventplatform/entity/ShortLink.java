package com.campus.eventplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("short_link")
public class ShortLink {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String shortCode;
    private String originalUrl;
    private Long eventId;
    private Long creatorId;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
    @TableLogic
    private Integer deleted;
}
