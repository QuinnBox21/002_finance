package com.quinn.finance.controller;

import com.quinn.core.common.controller.BaseController;
import com.quinn.core.common.model.Result;
import com.quinn.finance.dto.AuthRequest;
import com.quinn.finance.entity.User;
import com.quinn.finance.service.AuthenticationService;
import com.quinn.finance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证接口", description = "用户登录注册相关接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<AuthRequest.TokenResponse> login(@Valid @RequestBody AuthRequest.LoginRequest loginRequest) {
        try {
            // 调用认证服务进行登录验证
            AuthRequest.TokenResponse tokenResponse = authenticationService.login(loginRequest);
            return Result.success(tokenResponse);
        } catch (UsernameNotFoundException e) {
            // 用户不存在异常处理
            return Result.error(401, e.getMessage());
        } catch (Exception e) {
            // 其他异常处理
            return Result.error(500, "登录失败：" + e.getMessage());
        }
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody AuthRequest.RegisterRequest registerRequest) {
        User user = userService.register(registerRequest);
        return Result.success(user);
    }

    @Operation(summary = "用户退出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authenticationService.logout();
        return Result.success();
    }
}