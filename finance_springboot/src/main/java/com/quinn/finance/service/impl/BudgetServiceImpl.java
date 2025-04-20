package com.quinn.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quinn.core.common.model.PageResult;
import com.quinn.finance.dto.BudgetDTO;
import com.quinn.finance.entity.Budget;
import com.quinn.finance.entity.User;
import com.quinn.finance.mapper.BudgetMapper;
import com.quinn.finance.mapper.UserMapper;
import com.quinn.finance.service.BudgetService;
import com.quinn.finance.vo.BudgetVO;
import com.quinn.finance.vo.BudgetViewVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    private final UserMapper userMapper;

    public BudgetServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username)).getId();
    }

    @Transactional
    public Budget createBudget(BudgetDTO.CreateRequest request) {
        Budget budget = new Budget();
        budget.setUserId(getCurrentUserId());
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setRemark(request.getRemark());
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());

        save(budget);
        return budget;
    }

    @Transactional
    public Budget updateBudget(BudgetDTO.UpdateRequest request) {
        Budget budget = getById(request.getId());
        if (budget == null || !budget.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("预算不存在或无权限修改");
        }

        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budget.setMonth(request.getMonth());
        budget.setRemark(request.getRemark());
        budget.setUpdatedAt(LocalDateTime.now());

        updateById(budget);
        return budget;
    }

    @Transactional
    public void deleteBudget(Long id) {
        Budget budget = getById(id);
        if (budget == null || !budget.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("预算不存在或无权限删除");
        }

        removeById(id);
    }

    public BudgetViewVO queryBudgets(BudgetDTO.QueryRequest request) {
        Page<Budget> page = new Page<>(request.getPage(), request.getSize());
        IPage<Budget> iPage = getBaseMapper().queryBudgets(
                page,
                getCurrentUserId(),
                request.getMonth(),
                request.getCategory()
        );
        BigDecimal usedTotal = BigDecimal.ZERO;
        BigDecimal budgetTotal = BigDecimal.ZERO;

        // Calculate used and remaining amounts for each budget
        List<BudgetVO> budgetVOS = new ArrayList<>();
        for (Budget budget : iPage.getRecords()) {
            BudgetVO budgetVO = new BudgetVO();
            BigDecimal spentAmount = getBaseMapper().getSpentAmount(
                    getCurrentUserId(),
                    budget.getCategory(),
                    budget.getMonth()
            );
            BeanUtils.copyProperties(budget, budgetVO);
            budgetVO.setUsed(spentAmount);
            budgetVO.setRemaining(budget.getAmount().subtract(spentAmount));
            budgetVOS.add(budgetVO);
            usedTotal = usedTotal.add(spentAmount);
            budgetTotal = budgetTotal.add(budget.getAmount());
        }

        return BudgetViewVO.builder()
                .budgetList(PageResult.of(iPage.getTotal(), (int) iPage.getCurrent(), (int) iPage.getSize(), budgetVOS))
                .used(usedTotal)
                .total(budgetTotal)
                .build();
    }
}