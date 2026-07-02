package com.rollcall.controller;

import com.rollcall.dto.ChangePasswordRequest;
import com.rollcall.dto.RegisterRequest;
import com.rollcall.entity.User;
import com.rollcall.mapper.UserMapper;
import com.rollcall.util.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public Result register(@Valid @RequestBody RegisterRequest req) {
        try {
            String username = req.getUsername();
            User exist = userMapper.findByUsername(username);
            if (exist != null) return Result.error("用户已存在");
            User u = new User();
            u.setUsername(username);
            u.setPassword(passwordEncoder.encode(req.getPassword()));
            u.setRole(req.getRole() == null ? "ROLE_STUDENT" : req.getRole());
            userMapper.insertUser(u);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error("注册异常:" + e.getMessage());
        }
    }

    @PostMapping("/changePassword")
    public Result changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        try {
            User u = userMapper.findByUsername(req.getUsername());
            if (u == null) return Result.error("用户不存在");
            if (!passwordEncoder.matches(req.getOldPassword(), u.getPassword())) return Result.error("旧密码错误");
            String enc = passwordEncoder.encode(req.getNewPassword());
            userMapper.updatePassword(req.getUsername(), enc);
            return Result.success("密码修改成功");
        } catch (Exception e) {
            return Result.error("修改密码异常:" + e.getMessage());
        }
    }

    // admin/teacher privileged endpoints
    @PostMapping("/resetPassword")
    @PreAuthorize("hasRole('TEACHER')")
    public Result resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        try {
            User u = userMapper.findByUsername(username);
            if (u == null) return Result.error("用户不存在");
            String enc = passwordEncoder.encode(newPassword);
            userMapper.updatePassword(username, enc);
            return Result.success("密码已重置");
        } catch (Exception e) {
            return Result.error("重置密码异常:" + e.getMessage());
        }
    }

    @PostMapping("/changeRole")
    @PreAuthorize("hasRole('TEACHER')")
    public Result changeRole(@RequestParam String username, @RequestParam String role) {
        try {
            User u = userMapper.findByUsername(username);
            if (u == null) return Result.error("用户不存在");
            userMapper.updateRole(username, role);
            return Result.success("角色修改成功");
        } catch (Exception e) {
            return Result.error("修改角色异常:" + e.getMessage());
        }
    }

    @DeleteMapping("/deleteUser")
    @PreAuthorize("hasRole('TEACHER')")
    public Result deleteUser(@RequestParam String username) {
        try {
            int c = userMapper.deleteUser(username);
            if (c > 0) return Result.success("用户已删除");
            else return Result.error("用户不存在或删除失败");
        } catch (Exception e) {
            return Result.error("删除用户异常:" + e.getMessage());
        }
    }
}
