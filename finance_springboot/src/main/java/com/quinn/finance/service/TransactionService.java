package com.quinn.finance.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.quinn.core.common.model.PageResult;
import com.quinn.finance.dto.TransactionDTO;
import com.quinn.finance.entity.Transaction;

import java.time.LocalDate;

public interface TransactionService extends IService<Transaction> {
    public Transaction createTransaction(TransactionDTO.CreateRequest request);

    public Transaction updateTransaction(TransactionDTO.UpdateRequest request);

    public void deleteTransaction(Long id);

    public PageResult<Transaction> queryTransactions(TransactionDTO.QueryRequest request);

    public TransactionDTO.StatisticsResponse getStatistics(LocalDate startDate, LocalDate endDate);
}