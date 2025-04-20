package com.quinn.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdviceDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdviceResponse {
        private List<AdviceItem> advices;
        private long total;
        private int unreadCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdviceItem {
        private Long id;
        private String title;
        private String content;
        private String type; // income, expense, budget, investment, other
        private Integer priority; // 1-5, 5 being highest
        private LocalDateTime createdAt;
        private boolean read;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryRequest {
        private Integer page = 1;
        private Integer size = 10;
        private String type;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialOverview {
        private BigDecimal averageIncome;
        private BigDecimal averageExpense;
        private double incomeGrowth;  // 同比增长率，百分比
        private double expenseGrowth; // 同比增长率，百分比
    }
} 