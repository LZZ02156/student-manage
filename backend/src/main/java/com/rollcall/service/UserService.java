package com.rollcall.service;

import com.rollcall.entity.User;

public interface UserService {
    User findByUsername(String username);
}
