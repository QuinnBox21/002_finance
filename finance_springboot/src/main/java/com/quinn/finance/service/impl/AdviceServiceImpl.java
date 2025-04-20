package com.quinn.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quinn.finance.dto.AdviceDTO;
import com.quinn.finance.entity.Advice;
import com.quinn.finance.entity.Budget;
import com.quinn.finance.entity.Transaction;
import com.quinn.finance.entity.User;
import com.quinn.finance.mapper.AdviceMapper;
import com.quinn.finance.mapper.BudgetMapper;
import com.quinn.finance.mapper.TransactionMapper;
import com.quinn.finance.mapper.UserMapper;
import com.quinn.finance.service.AdviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdviceServiceImpl extends ServiceImpl<AdviceMapper, Advice> implements AdviceService {

    private final UserMapper userMapper;
    private final TransactionMapper transactionMapper;
    private final BudgetMapper budgetMapper;

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userMapper.selectOne(new QueryWrapper<User>().eq("username", username)).getId();
    }

    @Override
    public AdviceDTO.AdviceResponse queryAdvices(AdviceDTO.QueryRequest request) {
        Long userId = getCurrentUserId();
        
        QueryWrapper<Advice> queryWrapper = new QueryWrapper<Advice>()
                .eq("user_id", userId)
                .orderByDesc("priority")
                .orderByDesc("created_at");
        
        if (request.getType() != null && !request.getType().isEmpty()) {
            queryWrapper.eq("type", request.getType());
        }
        
        Page<Advice> page = new Page<>(request.getPage(), request.getSize());
        Page<Advice> advicePage = page(page, queryWrapper);
        
        // 未读数量
        int unreadCount = Math.toIntExact(count(new QueryWrapper<Advice>()
                .eq("user_id", userId)
                .eq("`read`", false)));
        
        List<AdviceDTO.AdviceItem> adviceItems = advicePage.getRecords().stream()
                .map(advice -> AdviceDTO.AdviceItem.builder()
                        .id(advice.getId())
                        .title(advice.getTitle())
                        .content(advice.getContent())
                        .type(advice.getType())
                        .priority(advice.getPriority())
                        .createdAt(advice.getCreatedAt())
                        .read(advice.isRead())
                        .build())
                .collect(Collectors.toList());
        
        return AdviceDTO.AdviceResponse.builder()
                .advices(adviceItems)
                .total(advicePage.getTotal())
                .unreadCount(unreadCount)
                .build();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Long userId = getCurrentUserId();
        getBaseMapper().markAsRead(id, userId);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        Long userId = getCurrentUserId();
        getBaseMapper().markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void deleteAdvice(Long id) {
        Long userId = getCurrentUserId();
        removeById(id);
    }

    @Override
    @Transactional
    public void generateAdvices() {
        Long userId = getCurrentUserId();
        List<Advice> advices = new ArrayList<>();
        
        // 当前月份
        String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 检查预算使用情况
        generateBudgetAdvices(userId, currentMonth, advices);
        
        // 检查收支趋势
        generateTransactionAdvices(userId, advices);
        
        // 保存生成的建议
        if (!advices.isEmpty()) {
            saveBatch(advices);
        }
    }
    
    @Override
    public AdviceDTO.FinancialOverview getFinancialOverview() {
        Long userId = getCurrentUserId();
        
        // 获取当前月和上个月
        LocalDate now = LocalDate.now();
        String currentMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String previousMonth = now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 获取当前月收支数据
        BigDecimal currentMonthIncome = calculateMonthlyIncome(userId, currentMonth);
        BigDecimal currentMonthExpense = calculateMonthlyExpense(userId, currentMonth);
        
        // 获取上月收支数据
        BigDecimal previousMonthIncome = calculateMonthlyIncome(userId, previousMonth);
        BigDecimal previousMonthExpense = calculateMonthlyExpense(userId, previousMonth);
        
        // 计算同比增长率
        double incomeGrowth = calculateGrowthRate(previousMonthIncome, currentMonthIncome);
        double expenseGrowth = calculateGrowthRate(previousMonthExpense, currentMonthExpense);
        
        return AdviceDTO.FinancialOverview.builder()
                .averageIncome(currentMonthIncome)
                .averageExpense(currentMonthExpense)
                .incomeGrowth(incomeGrowth)
                .expenseGrowth(expenseGrowth)
                .build();
    }
    
    private BigDecimal calculateMonthlyIncome(Long userId, String month) {
        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // 查询当月收入
        List<Transaction> incomeTransactions = transactionMapper.selectList(new QueryWrapper<Transaction>()
                .eq("user_id", userId)
                .eq("type", 1) // 收入
                .between("date", startDate, endDate));
        
        // 计算总收入
        return incomeTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private BigDecimal calculateMonthlyExpense(Long userId, String month) {
        LocalDate startDate = LocalDate.parse(month + "-01");
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        
        // 查询当月支出
        List<Transaction> expenseTransactions = transactionMapper.selectList(new QueryWrapper<Transaction>()
                .eq("user_id", userId)
                .eq("type", 0) // 支出
                .between("date", startDate, endDate));
        
        // 计算总支出
        return expenseTransactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    private double calculateGrowthRate(BigDecimal previous, BigDecimal current) {
        // 如果上月数据为0，则增长率为100%或0%
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100.0 : 0.0;
        }
        
        // 计算增长率：(当月-上月)/上月*100%
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
    
    private void generateBudgetAdvices(Long userId, String month, List<Advice> advices) {
        // 获取当月所有预算
        List<Budget> budgets = budgetMapper.selectList(new QueryWrapper<Budget>()
                .eq("user_id", userId)
                .eq("month", month));
        
        for (Budget budget : budgets) {
            // 计算预算使用情况
            BigDecimal spentAmount = budgetMapper.getSpentAmount(
                    userId,
                    budget.getCategory(),
                    month
            );
            
            // 如果已经使用超过80%，生成警告
            if (spentAmount.compareTo(budget.getAmount().multiply(BigDecimal.valueOf(0.8))) > 0) {
                Advice advice = new Advice();
                advice.setUserId(userId);
                advice.setTitle("预算即将超支");
                advice.setContent("您的" + budget.getCategory() + "类别预算已使用" + 
                        spentAmount.divide(budget.getAmount(), 2, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)) + 
                        "%，请注意控制支出。");
                advice.setType("budget");
                advice.setPriority(4);
                advice.setRead(false);
                advice.setCreatedAt(LocalDateTime.now());
                advice.setUpdatedAt(LocalDateTime.now());
                
                advices.add(advice);
            }
        }
    }
    
    private void generateTransactionAdvices(Long userId, List<Advice> advices) {
        // 获取最近一个月的交易
        LocalDate now = LocalDate.now();
        LocalDate oneMonthAgo = now.minusMonths(1);
        
        List<Transaction> transactions = transactionMapper.selectList(new QueryWrapper<Transaction>()
                .eq("user_id", userId)
                .ge("date", oneMonthAgo)
                .le("date", now));
        
        // 分析支出情况
        BigDecimal totalExpense = BigDecimal.ZERO;
        int expenseCount = 0;
        
        for (Transaction transaction : transactions) {
            if (transaction.getType() == 0) { // 支出
                totalExpense = totalExpense.add(transaction.getAmount());
                expenseCount++;
            }
        }
        
        // 如果一个月内没有任何收入，生成建议
        if (transactions.stream().noneMatch(t -> t.getType() == 1)) {
            Advice advice = new Advice();
            advice.setUserId(userId);
            advice.setTitle("缺乏收入记录");
            advice.setContent("在过去的一个月中，您没有记录任何收入。请确保记录所有收入以便更好地管理财务。");
            advice.setType("income");
            advice.setPriority(3);
            advice.setRead(false);
            advice.setCreatedAt(LocalDateTime.now());
            advice.setUpdatedAt(LocalDateTime.now());
            
            advices.add(advice);
        }
        
        // 如果平均每天超过3笔支出，提醒注意消费频率
        if (expenseCount > 90) { // 30天*3笔/天
            Advice advice = new Advice();
            advice.setUserId(userId);
            advice.setTitle("消费频率较高");
            advice.setContent("在过去的一个月中，您平均每天消费超过3次。高频消费可能导致冲动消费，建议合并采购并减少消费频率。");
            advice.setType("expense");
            advice.setPriority(2);
            advice.setRead(false);
            advice.setCreatedAt(LocalDateTime.now());
            advice.setUpdatedAt(LocalDateTime.now());
            
            advices.add(advice);
        }
    }
}