package com.campus.eventplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.eventplatform.dto.ShortLinkReq;
import com.campus.eventplatform.entity.ShortLink;

public interface ShortLinkService extends IService<ShortLink> {
    ShortLink createShortLink(ShortLinkReq req, String username);
    String getOriginalUrl(String shortCode);
}
