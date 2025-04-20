package com.quinn.finance.service;

import com.quinn.finance.dto.AuthRequest;
import com.quinn.finance.entity.User;

public interface UserService{
    public User register(AuthRequest.RegisterRequest registerRequest);
}