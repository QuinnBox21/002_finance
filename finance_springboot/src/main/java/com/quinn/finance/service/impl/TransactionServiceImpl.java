package com.quinn.finance.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quinn.core.common.model.PageResult;
import com.quinn.finance.dto.TransactionDTO;
import com.quinn.finance.entity.Transaction;
import com.quinn.finance.entity.User;
import com.quinn.finance.mapper.TransactionMapper;
import com.quinn.finance.mapper.UserMapper;
import com.quinn.finance.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl extends ServiceImpl<TransactionMapper, Transaction> implements TransactionService {

    private final UserMapper userMapper;


    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("username", username));
        return null != user ? user.getId() : null;
    }

    @Transactional
    public Transaction createTransaction(TransactionDTO.CreateRequest request) {
        Transaction transaction = new Transaction();
        transaction.setUserId(getCurrentUserId());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setRemark(request.getRemark());
        transaction.setDate(request.getDate());
        transaction.setCreatedAt(LocalDateTime.now());

        save(transaction);
        return transaction;
    }

    @Transactional
    public Transaction updateTransaction(TransactionDTO.UpdateRequest request) {
        Transaction transaction = getById(request.getId());
        if (transaction == null || !transaction.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("交易记录不存在或无权限修改");
        }

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setCategory(request.getCategory());
        transaction.setRemark(request.getRemark());
        transaction.setDate(request.getDate());

        updateById(transaction);
        return transaction;
    }

    @Transactional
    public void deleteTransaction(Long id) {
        Transaction transaction = getById(id);
        if (transaction == null || !transaction.getUserId().equals(getCurrentUserId())) {
            throw new RuntimeException("交易记录不存在或无权限删除");
        }

        removeById(id);
    }

    public PageResult<Transaction> queryTransactions(TransactionDTO.QueryRequest request) {
        Page<Transaction> page = new Page<>(request.getPage(), request.getSize());
        IPage<Transaction> iPage = getBaseMapper().queryTransactions(
                page,
                getCurrentUserId(),
                request.getStartDate(),
                request.getEndDate(),
                request.getType(),
                request.getCategory()
        );

        return PageResult.of(iPage.getTotal(), (int) iPage.getCurrent(), (int) iPage.getSize(), iPage.getRecords());
    }

    public TransactionDTO.StatisticsResponse getStatistics(LocalDate startDate, LocalDate endDate) {
        Long userId = getCurrentUserId();
        BigDecimal totalIncome = getBaseMapper().sumAmountByType(userId, 1, startDate, endDate);
        BigDecimal totalExpense = getBaseMapper().sumAmountByType(userId, 0, startDate, endDate);

        return new TransactionDTO.StatisticsResponse(totalIncome, totalExpense);
    }
}