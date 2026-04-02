package com.campus.eventplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("event")
public class Event {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String title;
    private String description;
    private String status; // DRAFT, PUBLISHED, CANCELLED, ENDED
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long creatorId;
    private Integer capacity;
    @TableLogic
    private Integer deleted;
}
