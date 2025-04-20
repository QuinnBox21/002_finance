package com.quinn.finance.controller;

import com.quinn.core.common.controller.BaseController;
import com.quinn.core.common.model.Result;
import com.quinn.finance.dto.BudgetDTO;
import com.quinn.finance.entity.Budget;
import com.quinn.finance.service.BudgetService;
import com.quinn.finance.vo.BudgetViewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
@RequiredArgsConstructor
public class BudgetController extends BaseController {

    private final BudgetService budgetService;

    @PostMapping
    public Result<Budget> createBudget(@RequestBody BudgetDTO.CreateRequest request) {
        return Result.success(budgetService.createBudget(request));
    }

    @PutMapping("/{id}")
    public Result<Budget> updateBudget(@PathVariable Long id,
                                             @RequestBody BudgetDTO.UpdateRequest request) {
        request.setId(id);
        return Result.success(budgetService.updateBudget(request));
    }

    @DeleteMapping("/{id}")
    public  Result<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return Result.success();
    }

    @GetMapping
    public Result<BudgetViewVO> queryBudgets(BudgetDTO.QueryRequest request) {
        return Result.success(budgetService.queryBudgets(request));
    }

}