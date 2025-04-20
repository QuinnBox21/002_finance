package com.quinn.finance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.quinn.core.common.controller.BaseController;
import com.quinn.core.common.model.PageResult;
import com.quinn.core.common.model.Result;
import com.quinn.finance.dto.TransactionDTO;
import com.quinn.finance.entity.Transaction;
import com.quinn.finance.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transaction", description = "交易记录相关接口")
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController extends BaseController {

    private final TransactionService transactionService;

    @Operation(summary = "创建交易记录")
    @PostMapping
    public Result<Void> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionDTO.CreateRequest request) {
        transactionService.createTransaction(request);
        return Result.success();
    }

    @Operation(summary = "更新交易记录")
    @PutMapping("/{id}")
    public Result<Void> updateTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TransactionDTO.UpdateRequest request) {
        request.setId(id);
        transactionService.updateTransaction(request);
        return Result.success();
    }

    @Operation(summary = "删除交易记录")
    @DeleteMapping("/{id}")
    public Result<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return Result.success();
    }

    @Operation(summary = "查询交易记录列表")
    @GetMapping
    public Result<PageResult<Transaction>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid TransactionDTO.QueryRequest request) {
        return Result.success(transactionService.queryTransactions(request));
    }
}