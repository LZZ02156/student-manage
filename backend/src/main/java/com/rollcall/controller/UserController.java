package com.rollcall.controller;

import com.rollcall.entity.User;
import com.rollcall.mapper.UserMapper;
import com.rollcall.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public Result register(@RequestParam String username, @RequestParam String password, @RequestParam(required = false) String role) {
        try {
            User exist = userMapper.findByUsername(username);
            if (exist != null) return Result.error("用户已存在");
            User u = new User();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(password));
            u.setRole(role == null ? "ROLE_STUDENT" : role);
            userMapper.insertUser(u);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error("注册异常:" + e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public Result changePassword(@RequestParam String username, @RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            User u = userMapper.findByUsername(username);
            if (u == null) return Result.error("用户不存在");
            if (!passwordEncoder.matches(oldPassword, u.getPassword())) return Result.error("旧密码错误");
            String enc = passwordEncoder.encode(newPassword);
            userMapper.updatePassword(username, enc);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("修改密码异常:" + e.getMessage());
        }
    }
}
