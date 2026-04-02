package com.campus.eventplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("enrollment")
public class Enrollment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long eventId;
    private Long userId;
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    private String checkinStatus; // UNCHECKED, CHECKED
    private LocalDateTime checkinTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}
