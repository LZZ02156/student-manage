package com.rollcall.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "username 不能为空")
    @Size(min = 3, max = 50, message = "username 长度应在 3-50 之间")
    private String username;

    @NotBlank(message = "password 不能为空")
    @Size(min = 6, max = 128, message = "password 长度应至少 6")
    private String password;

    private String role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
