package com.quinn.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.quinn.finance.dto.DashboardDTO;
import com.quinn.finance.entity.Budget;
import com.quinn.finance.entity.Transaction;
import com.quinn.finance.entity.User;
import com.quinn.finance.mapper.BudgetMapper;
import com.quinn.finance.mapper.TransactionMapper;
import com.quinn.finance.mapper.UserMapper;
import com.quinn.finance.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TransactionMapper transactionMapper;
    private final BudgetMapper budgetMapper;
    private final UserMapper userMapper;


    @Override
    public DashboardDTO.Response getDashboardData(String userName) throws Exception{
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = now.withDayOfMonth(1);
        LocalDate lastDayOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, userName));
        if (null == user)
            throw new RuntimeException("用户不存在");
        Long userId = user.getId();
        // 获取当月预算
        BigDecimal monthBudget = getMonthBudget(userId);

        // 获取当月收支数据
        List<Transaction> currentMonthTransactions = getMonthTransactions(userId, firstDayOfMonth, lastDayOfMonth);
        BigDecimal monthIncome = calculateTotalAmount(currentMonthTransactions, 1);
        BigDecimal monthExpense = calculateTotalAmount(currentMonthTransactions, 0);

        // 获取上月收支数据，计算同比增长
        LocalDate lastMonth = now.minusMonths(1);
        List<Transaction> lastMonthTransactions = getMonthTransactions(userId,
                lastMonth.withDayOfMonth(1),
                lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()));
        BigDecimal lastMonthIncome = calculateTotalAmount(lastMonthTransactions, 1);
        BigDecimal lastMonthExpense = calculateTotalAmount(lastMonthTransactions, 0);

        // 计算收支增长率
        double incomeGrowth = calculateGrowthRate(monthIncome, lastMonthIncome);
        double expenseGrowth = calculateGrowthRate(monthExpense, lastMonthExpense);

        // 获取支出类别统计
        List<DashboardDTO.CategoryStats> categoryStats = calculateCategoryStats(currentMonthTransactions);

        // 构建响应数据
        DashboardDTO.Response response = new DashboardDTO.Response();
        response.setBudget(monthBudget);
        response.setExpense(monthExpense);
        response.setIncome(monthIncome);
        response.setIncomeGrowth(incomeGrowth);
        response.setExpenseGrowth(expenseGrowth);
        response.setCategories(categoryStats);

        return response;
    }

    private BigDecimal getMonthBudget(Long userId) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();

        // 定义日期时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // 格式化当前日期，只保留年份和月份
        String month = currentDate.format(formatter);

        List<Budget> budgets = budgetMapper.selectList(new LambdaQueryWrapper<Budget>()
                .eq(Budget::getUserId, userId)
                .eq(Budget::getMonth, month));
        return budgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Transaction> getMonthTransactions(Long userId, LocalDate startDate, LocalDate endDate) {
        return transactionMapper.selectList(new LambdaQueryWrapper<Transaction>()
                .eq(Transaction::getUserId, userId)
                .between(Transaction::getDate, startDate, endDate));
    }

    private BigDecimal calculateTotalAmount(List<Transaction> transactions, int type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private double calculateGrowthRate(BigDecimal current, BigDecimal previous) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private List<DashboardDTO.CategoryStats> calculateCategoryStats(List<Transaction> transactions) {
        // 只统计支出类别
        List<Transaction> expenses = transactions.stream()
                .filter(t -> t.getType() == 0)
                .collect(Collectors.toList());

        BigDecimal totalExpense = calculateTotalAmount(expenses, 0);

        // 按类别分组统计
        Map<String, BigDecimal> categoryAmounts = expenses.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategory,
                        Collectors.reducing(BigDecimal.ZERO,
                                Transaction::getAmount,
                                BigDecimal::add)));

        // 转换为CategoryStats列表并计算百分比
        List<DashboardDTO.CategoryStats> stats = new ArrayList<>();
        categoryAmounts.forEach((category, amount) -> {
            DashboardDTO.CategoryStats stat = new DashboardDTO.CategoryStats();
            stat.setName(category);
            stat.setAmount(amount);
            stat.setPercentage(totalExpense.compareTo(BigDecimal.ZERO) == 0 ? 0.0 :
                    amount.divide(totalExpense, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue());
            stats.add(stat);
        });

        // 按金额降序排序并返回前5个
        return stats.stream()
                .sorted(Comparator.comparing(DashboardDTO.CategoryStats::getAmount).reversed())
                .limit(5)
                .collect(Collectors.toList());
    }
}