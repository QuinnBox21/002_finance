package com.quinn.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionDTO {

    @Data
    public static class CreateRequest {
        @NotNull(message = "金额不能为空")
        @DecimalMin(value = "0.01", message = "金额必须大于0")
        private BigDecimal amount;

        @NotNull(message = "类型不能为空")
        private Integer type;  // 0: 支出, 1: 收入

        @NotBlank(message = "类别不能为空")
        private String category;

        private String remark;

        @NotNull(message = "日期不能为空")
        private LocalDate date;
    }

    @Data
    public static class UpdateRequest extends CreateRequest {
        @NotNull(message = "ID不能为空")
        private Long id;
    }

    @Data
    public static class QueryRequest {
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer type;
        private String category;
        private Integer page = 1;
        private Integer size = 10;
    }

    @Data
    public static class StatisticsResponse {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal balance;

        public StatisticsResponse(BigDecimal totalIncome, BigDecimal totalExpense) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.balance = totalIncome.subtract(totalExpense);
        }
    }
}