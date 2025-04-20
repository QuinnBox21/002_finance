package com.quinn.finance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quinn.finance.entity.Advice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AdviceMapper extends BaseMapper<Advice> {
    
    @Update("UPDATE fin_advice SET `read` = true WHERE id = #{id} AND user_id = #{userId}")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);
    
    @Update("UPDATE fin_advice SET `read` = true WHERE user_id = #{userId}")
    int markAllAsRead(@Param("userId") Long userId);
}