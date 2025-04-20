package com.quinn.finance.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BudgetVO {
    private Long id;
    private Long userId;
    private String category;
    private BigDecimal amount;
    private String month;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private BigDecimal used;
    private BigDecimal remaining;
}
