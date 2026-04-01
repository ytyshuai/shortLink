CREATE DATABASE IF NOT EXISTS campus_event_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_event_db;

CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: ADMIN, USER',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 初始化一个管理员账号 (密码为 123456 的 BCrypt 密文)
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `role`) VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', 'ADMIN');

CREATE TABLE IF NOT EXISTS `event` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `title` VARCHAR(100) NOT NULL COMMENT '活动标题',
    `description` TEXT COMMENT '活动描述',
    `location` VARCHAR(100) NOT NULL COMMENT '活动地点',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `capacity` INT NOT NULL DEFAULT 0 COMMENT '活动容量(0表示不限制)',
    `status` VARCHAR(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态: DRAFT(草稿), PUBLISHED(已发布), CANCELLED(已取消)',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
     `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
     INDEX `idx_creator_id` (`creator_id`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动表';

CREATE TABLE IF NOT EXISTS `short_link` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `short_code` VARCHAR(10) NOT NULL UNIQUE COMMENT '短链码(Base62)',
    `original_url` VARCHAR(1000) NOT NULL COMMENT '原始长链接',
    `event_id` BIGINT COMMENT '关联的活动ID(可选)',
    `creator_id` BIGINT COMMENT '创建者ID(可选)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `expire_time` DATETIME COMMENT '过期时间(可选)',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0未删除, 1已删除',
    INDEX `idx_short_code` (`short_code`),
    INDEX `idx_event_id` (`event_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='短链接表';