package com.quinn.finance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

public class DashboardDTO {

    @Data
    public static class Response {
        private BigDecimal budget;           // 月度预算
        private BigDecimal expense;          // 月度支出
        private BigDecimal income;           // 月度收入
        private Double incomeGrowth;         // 收入同比增长率
        private Double expenseGrowth;        // 支出同比增长率
        private List<CategoryStats> categories;  // 支出类别统计
    }

    @Data
    public static class CategoryStats {
        private String name;            // 类别名称
        private BigDecimal amount;      // 类别金额
        private Double percentage;      // 占比百分比
    }
}