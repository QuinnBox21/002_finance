package com.quinn.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.quinn.finance.dto.ReportDTO;
import com.quinn.finance.dto.ReportDTO.CategoryData;
import com.quinn.finance.dto.ReportDTO.ReportResponse;
import com.quinn.finance.dto.ReportDTO.StatisticsData;
import com.quinn.finance.dto.ReportDTO.TrendData;
import com.quinn.finance.entity.Budget;
import com.quinn.finance.entity.Transaction;
import com.quinn.finance.entity.User;
import com.quinn.finance.mapper.BudgetMapper;
import com.quinn.finance.mapper.TransactionMapper;
import com.quinn.finance.mapper.UserMapper;
import com.quinn.finance.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionMapper transactionMapper;
    private final BudgetMapper budgetMapper;
    private final UserMapper userMapper;

    @Override
    public ReportResponse getReportData(String startMonth, String endMonth) {
        Long userId = getCurrentUserId();
        List<Transaction> transactions = getTransactionsForPeriod(userId, startMonth, endMonth);
        
        List<String> monthList = generateMonthList(startMonth, endMonth);
        
        // Process data for trend chart
        TrendData trendData = generateTrendData(transactions, monthList);
        
        // Process data for category charts
        List<CategoryData> expenseCategories = generateCategoryData(transactions, 0); // 0 = expense
        List<CategoryData> incomeCategories = generateCategoryData(transactions, 1);  // 1 = income
        
        // 处理每月统计数据
        List<StatisticsData> statistics = generateStatistics(transactions, monthList, userId);
        
        return ReportDTO.ReportResponse.builder()
                .trend(trendData)
                .expenseCategories(expenseCategories)
                .incomeCategories(incomeCategories)
                .statistics(statistics)
                .build();
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username)).getId();
    }

    private List<Transaction> getTransactionsForPeriod(Long userId, String startMonth, String endMonth) {
        LocalDate startDate = LocalDate.parse(startMonth + "-01");
        LocalDate endDate = LocalDate.parse(endMonth + "-01").plusMonths(1).minusDays(1);
        
        return transactionMapper.selectList(new QueryWrapper<Transaction>()
                .eq("user_id", userId)
                .between("date", startDate, endDate)
                .orderByAsc("date"));
    }

    private List<String> generateMonthList(String startMonth, String endMonth) {
        List<String> months = new ArrayList<>();
        
        LocalDate currentDate = LocalDate.parse(startMonth + "-01");
        LocalDate endDate = LocalDate.parse(endMonth + "-01");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        while (!currentDate.isAfter(endDate)) {
            months.add(currentDate.format(formatter));
            currentDate = currentDate.plusMonths(1);
        }
        
        return months;
    }

    private TrendData generateTrendData(List<Transaction> transactions, List<String> monthList) {
        Map<String, BigDecimal> incomeByMonth = new HashMap<>();
        Map<String, BigDecimal> expenseByMonth = new HashMap<>();
        Map<String, Integer> incomeCountByMonth = new HashMap<>();
        Map<String, Integer> expenseCountByMonth = new HashMap<>();
        
        // Initialize maps
        for (String month : monthList) {
            incomeByMonth.put(month, BigDecimal.ZERO);
            expenseByMonth.put(month, BigDecimal.ZERO);
            incomeCountByMonth.put(month, 0);
            expenseCountByMonth.put(month, 0);
        }
        
        // Process transactions
        for (Transaction transaction : transactions) {
            String month = transaction.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            if (transaction.getType() == 1) { // Income
                incomeByMonth.put(month, incomeByMonth.get(month).add(transaction.getAmount()));
                incomeCountByMonth.put(month, incomeCountByMonth.get(month) + 1);
            } else { // Expense
                expenseByMonth.put(month, expenseByMonth.get(month).add(transaction.getAmount()));
                expenseCountByMonth.put(month, expenseCountByMonth.get(month) + 1);
            }
        }
        
        // Convert maps to lists in the order of monthList
        List<BigDecimal> incomeData = monthList.stream()
                .map(incomeByMonth::get)
                .collect(Collectors.toList());
                
        List<BigDecimal> expenseData = monthList.stream()
                .map(expenseByMonth::get)
                .collect(Collectors.toList());
                
        List<Integer> incomeCountData = monthList.stream()
                .map(incomeCountByMonth::get)
                .collect(Collectors.toList());
                
        List<Integer> expenseCountData = monthList.stream()
                .map(expenseCountByMonth::get)
                .collect(Collectors.toList());
        
        return TrendData.builder()
                .months(monthList)
                .income(incomeData)
                .expense(expenseData)
                .incomeCount(incomeCountData)
                .expenseCount(expenseCountData)
                .build();
    }

    private List<CategoryData> generateCategoryData(List<Transaction> transactions, int type) {
        Map<String, BigDecimal> categoryAmounts = new HashMap<>();
        
        // Group transactions by category and sum amounts
        for (Transaction transaction : transactions) {
            if (transaction.getType() == type) {
                String category = transaction.getCategory();
                BigDecimal currentAmount = categoryAmounts.getOrDefault(category, BigDecimal.ZERO);
                categoryAmounts.put(category, currentAmount.add(transaction.getAmount()));
            }
        }
        
        // Convert to list of CategoryData
        return categoryAmounts.entrySet().stream()
                .map(entry -> CategoryData.builder()
                        .name(entry.getKey())
                        .value(entry.getValue())
                        .build())
                .sorted(Comparator.comparing(CategoryData::getValue).reversed())
                .collect(Collectors.toList());
    }

    private List<StatisticsData> generateStatistics(List<Transaction> transactions, List<String> monthList, Long userId) {
        Map<String, BigDecimal> incomeByMonth = new HashMap<>();
        Map<String, BigDecimal> expenseByMonth = new HashMap<>();
        Map<String, Integer> incomeCountByMonth = new HashMap<>();
        Map<String, Integer> expenseCountByMonth = new HashMap<>();
        
        // Initialize maps
        for (String month : monthList) {
            incomeByMonth.put(month, BigDecimal.ZERO);
            expenseByMonth.put(month, BigDecimal.ZERO);
            incomeCountByMonth.put(month, 0);
            expenseCountByMonth.put(month, 0);
        }
        
        // Process transactions
        for (Transaction transaction : transactions) {
            String month = transaction.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM"));
            
            if (transaction.getType() == 1) { // Income
                incomeByMonth.put(month, incomeByMonth.getOrDefault(month, BigDecimal.ZERO).add(transaction.getAmount()));
                incomeCountByMonth.put(month, incomeCountByMonth.getOrDefault(month, 0) + 1);
            } else { // Expense
                expenseByMonth.put(month, expenseByMonth.getOrDefault(month, BigDecimal.ZERO).add(transaction.getAmount()));
                expenseCountByMonth.put(month, expenseCountByMonth.getOrDefault(month, 0) + 1);
            }
        }
        
        // Generate statistics for each month
        List<StatisticsData> statisticsList = new ArrayList<>();
        
        for (String month : monthList) {
            BigDecimal income = incomeByMonth.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal expense = expenseByMonth.getOrDefault(month, BigDecimal.ZERO);
            BigDecimal balance = income.subtract(expense);
            
            // Calculate budget usage percentage
            Double budgetUsage = calculateBudgetUsageForMonth(userId, month, expense);
            
            statisticsList.add(StatisticsData.builder()
                    .month(month)
                    .income(income)
                    .expense(expense)
                    .balance(balance)
                    .budgetUsage(budgetUsage)
                    .incomeCount(incomeCountByMonth.getOrDefault(month, 0))
                    .expenseCount(expenseCountByMonth.getOrDefault(month, 0))
                    .build());
        }
        
        return statisticsList;
    }

    private Double calculateBudgetUsageForMonth(Long userId, String month, BigDecimal totalExpense) {
        // Get all budgets for the month
        List<Budget> budgets = budgetMapper.selectList(new QueryWrapper<Budget>()
                .eq("user_id", userId)
                .eq("month", month));
        
        // Calculate total budget amount
        BigDecimal totalBudget = budgets.stream()
                .map(Budget::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calculate usage percentage
        if (totalBudget.compareTo(BigDecimal.ZERO) > 0) {
            return totalExpense.multiply(BigDecimal.valueOf(100))
                    .divide(totalBudget, 2, RoundingMode.HALF_UP)
                    .doubleValue();
        }
        
        // If no budget, return 0 usage
        return 0.0;
    }
} 