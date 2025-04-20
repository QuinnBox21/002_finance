package com.quinn.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quinn.finance.dto.AuthRequest;
import com.quinn.finance.entity.User;
import com.quinn.finance.mapper.UserMapper;
import com.quinn.finance.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User findByUsername(String username) {
        return getOne(new QueryWrapper<User>().eq("username", username));
    }


    @Transactional
    public User register(AuthRequest.RegisterRequest registerRequest) {
        // 检查用户名是否已存在
        if (count(new QueryWrapper<User>().eq("username", registerRequest.getUsername())) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (count(new QueryWrapper<User>().eq("email", registerRequest.getEmail())) > 0) {
            throw new RuntimeException("邮箱已被使用");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());

        // 保存用户
        save(user);
        return user;
    }
}