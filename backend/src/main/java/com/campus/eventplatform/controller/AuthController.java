package com.campus.eventplatform.controller;

import com.campus.eventplatform.common.Result;
import com.campus.eventplatform.dto.LoginReq;
import com.campus.eventplatform.dto.RegisterReq;
import com.campus.eventplatform.entity.User;
import com.campus.eventplatform.service.UserService;
import com.campus.eventplatform.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public Result<Map<String, String>> login(@Valid @RequestBody LoginReq req) {
        User user = userService.findByUsername(req.getUsername());
        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            return Result.error(401, "用户名或密码错误");
        }
        
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole());
        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", user.getUsername());
        data.put("role", user.getRole());
        
        return Result.success(data);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterReq req) {
        try {
            userService.register(req.getUsername(), req.getPassword());
            return Result.success(null);
        } catch (Exception e) {
            return Result.error(400, e.getMessage());
        }
    }
}