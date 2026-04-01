package com.campus.eventplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.eventplatform.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
}