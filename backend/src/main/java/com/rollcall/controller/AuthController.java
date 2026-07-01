package com.rollcall.controller;

import com.rollcall.entity.User;
import com.rollcall.mapper.UserMapper;
import com.rollcall.util.JwtUtil;
import com.rollcall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userMapper.findByUsername(username);
            if (user == null) return Result.error("用户不存在");
            if (!passwordEncoder.matches(password, user.getPassword())) return Result.error("密码错误");
            String token = JwtUtil.generateToken(username);
            return Result.success("登录成功", java.util.Map.of("token", token));
        } catch (Exception e) {
            return Result.error("登录异常:" + e.getMessage());
        }
    }
}
