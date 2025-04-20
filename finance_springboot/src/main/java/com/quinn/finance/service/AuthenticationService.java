package com.quinn.finance.service;

import com.quinn.finance.dto.AuthRequest;

public interface AuthenticationService {

    public void logout();

    public AuthRequest.TokenResponse login(AuthRequest.LoginRequest loginRequest);
}