package com.quinn.finance.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("fin_advice")
public class Advice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;     // income, expense, budget, investment, other
    private Integer priority; // 1-5, 5 being highest
    
    @TableField("`read`")
    private boolean read;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}