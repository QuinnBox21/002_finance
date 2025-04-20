package com.quinn.finance.service;

import com.quinn.finance.dto.BudgetDTO;
import com.quinn.finance.entity.Budget;
import com.quinn.finance.vo.BudgetViewVO;

import java.util.List;

public interface BudgetService {

    public Budget createBudget(BudgetDTO.CreateRequest request);

    public Budget updateBudget(BudgetDTO.UpdateRequest request);

    public void deleteBudget(Long id);

    public BudgetViewVO queryBudgets(BudgetDTO.QueryRequest request);

}