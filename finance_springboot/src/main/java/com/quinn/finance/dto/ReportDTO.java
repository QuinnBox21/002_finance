package com.quinn.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class ReportDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportResponse {
        private TrendData trend;
        private List<CategoryData> expenseCategories;
        private List<CategoryData> incomeCategories;
        private List<StatisticsData> statistics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private List<String> months;
        private List<BigDecimal> income;
        private List<BigDecimal> expense;
        private List<Integer> incomeCount;
        private List<Integer> expenseCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryData {
        private BigDecimal value;
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatisticsData {
        private String month;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;
        private Double budgetUsage;
        private Integer incomeCount;
        private Integer expenseCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportRequest {
        private String startMonth;
        private String endMonth;
    }
} 