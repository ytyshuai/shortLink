package com.campus.eventplatform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.eventplatform.dto.ShortLinkReq;
import com.campus.eventplatform.entity.ShortLink;
import com.campus.eventplatform.entity.User;
import com.campus.eventplatform.mapper.ShortLinkMapper;
import com.campus.eventplatform.service.ShortLinkService;
import com.campus.eventplatform.service.UserService;
import com.campus.eventplatform.util.Base62Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLink> implements ShortLinkService {

    @Autowired
    private UserService userService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "shortlink:";

    @Override
    public ShortLink createShortLink(ShortLinkReq req, String username) {
        User user = userService.findByUsername(username);
        
        ShortLink shortLink = new ShortLink();
        BeanUtils.copyProperties(req, shortLink);
        if (user != null) {
            shortLink.setCreatorId(user.getId());
        }
        
        // 1. 先保存获取自增ID
        this.save(shortLink);
        
        // 2. 根据ID生成Base62短链码
        String shortCode = Base62Utils.encode(shortLink.getId());
        shortLink.setShortCode(shortCode);
        
        // 3. 更新短链码
        this.updateById(shortLink);
        
        // 4. 写入Redis缓存
        long expireSeconds = -1;
        if (req.getExpireTime() != null) {
            expireSeconds = java.time.Duration.between(LocalDateTime.now(), req.getExpireTime()).getSeconds();
        }
        
        if (expireSeconds > 0) {
            redisTemplate.opsForValue().set(CACHE_PREFIX + shortCode, req.getOriginalUrl(), expireSeconds, TimeUnit.SECONDS);
        } else {
            // 默认缓存30天
            redisTemplate.opsForValue().set(CACHE_PREFIX + shortCode, req.getOriginalUrl(), 30, TimeUnit.DAYS);
        }
        
        return shortLink;
    }

    @Override
    public String getOriginalUrl(String shortCode) {
        // 1. 先查缓存
        String originalUrl = redisTemplate.opsForValue().get(CACHE_PREFIX + shortCode);
        if (originalUrl != null) {
            return originalUrl;
        }
        
        // 2. 缓存未命中查数据库
        ShortLink shortLink = this.getOne(new LambdaQueryWrapper<ShortLink>()
                .eq(ShortLink::getShortCode, shortCode));
                
        if (shortLink == null) {
            throw new RuntimeException("短链接不存在");
        }
        
        if (shortLink.getExpireTime() != null && shortLink.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("短链接已过期");
        }
        
        // 3. 回写缓存
        long expireSeconds = -1;
        if (shortLink.getExpireTime() != null) {
            expireSeconds = java.time.Duration.between(LocalDateTime.now(), shortLink.getExpireTime()).getSeconds();
        }
        
        if (expireSeconds > 0) {
            redisTemplate.opsForValue().set(CACHE_PREFIX + shortCode, shortLink.getOriginalUrl(), expireSeconds, TimeUnit.SECONDS);
        } else {
            redisTemplate.opsForValue().set(CACHE_PREFIX + shortCode, shortLink.getOriginalUrl(), 30, TimeUnit.DAYS);
        }
        
        return shortLink.getOriginalUrl();
    }
}
