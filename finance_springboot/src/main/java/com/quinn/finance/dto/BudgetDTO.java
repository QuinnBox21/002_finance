package com.quinn.finance.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BudgetDTO {
    @Data
    public static class CreateRequest {
        private String category;
        private BigDecimal amount;
        private String month;
        private String remark;
    }

    @Data
    public static class UpdateRequest {
        private Long id;
        private String category;
        private BigDecimal amount;
        private String month;
        private String remark;
    }

    @Data
    public static class QueryRequest {
        private String month;
        private String category;
        private Integer page = 1;
        private Integer size = 10;
    }
}