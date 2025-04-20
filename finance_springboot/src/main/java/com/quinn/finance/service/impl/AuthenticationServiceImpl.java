package com.quinn.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quinn.finance.dto.AuthRequest;
import com.quinn.finance.entity.User;
import com.quinn.finance.mapper.UserMapper;
import com.quinn.finance.security.JwtTokenProvider;
import com.quinn.finance.service.AuthenticationService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthenticationServiceImpl implements AuthenticationService, UserDetailsService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    public AuthenticationServiceImpl(JwtTokenProvider jwtTokenProvider,
                                     UserMapper userMapper) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Override
    public void logout() {
        // 执行令牌失效逻辑
        SecurityContextHolder.clearContext();
    }
    public AuthRequest.TokenResponse login(AuthRequest.LoginRequest loginRequest) {
        User user = userMapper.selectOne(new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                .eq(User::getUsername, loginRequest.getUsername())
                .last("LIMIT 1"));

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 获取UserDetails对象
        UserDetails userDetails = loadUserByUsername(user.getUsername());

        // 创建认证令牌
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // 生成JWT令牌
        String token = jwtTokenProvider.generateToken(authentication);

        // 返回令牌响应
        AuthRequest.UserResponse userResponse = new AuthRequest.UserResponse();
        BeanUtils.copyProperties(user, userResponse);
        return new AuthRequest.TokenResponse(token, userResponse);
    }
}
