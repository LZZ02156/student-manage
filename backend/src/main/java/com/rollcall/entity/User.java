package com.rollcall.entity;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String username;
    private String password; // stored as bcrypt
    private String role; // ROLE_TEACHER or ROLE_STUDENT
}
