package com.quinn.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quinn.finance.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}