package com.campus.eventplatform.controller;

import com.campus.eventplatform.common.Result;
import com.campus.eventplatform.dto.ShortLinkReq;
import com.campus.eventplatform.entity.ShortLink;
import com.campus.eventplatform.service.ShortLinkService;
import com.campus.eventplatform.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/short-link")
public class ShortLinkController {

    @Autowired
    private ShortLinkService shortLinkService;

    @Autowired
    private StatisticsService statisticsService;

    @PostMapping("/create")
    public Result<ShortLink> createShortLink(@Valid @RequestBody ShortLinkReq req, Authentication authentication) {
        try {
            String username = authentication != null ? authentication.getName() : null;
            ShortLink shortLink = shortLinkService.createShortLink(req, username);
            return Result.success(shortLink);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {
        try {
            String originalUrl = shortLinkService.getOriginalUrl(shortCode);
            // 埋点落库：每次重定向增加访问量统计
            statisticsService.incrementShortLinkVisit(shortCode);
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(originalUrl))
                    .build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
