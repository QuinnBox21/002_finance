package com.quinn.finance.vo;

import com.quinn.core.common.model.PageResult;
import com.quinn.finance.entity.Budget;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class BudgetViewVO {
    private PageResult<BudgetVO> budgetList;
    private BigDecimal used;
    private BigDecimal total;
}
