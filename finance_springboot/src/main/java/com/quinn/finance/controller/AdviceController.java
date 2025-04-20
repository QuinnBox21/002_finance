package com.quinn.finance.controller;

import com.quinn.core.common.controller.BaseController;
import com.quinn.core.common.model.Result;
import com.quinn.finance.dto.AdviceDTO;
import com.quinn.finance.service.AdviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/advices")
@RequiredArgsConstructor
public class AdviceController extends BaseController {

    private final AdviceService adviceService;

    /**
     * 获取财务概览数据
     * @return 财务概览数据
     */
    @GetMapping("/overview")
    public Result<AdviceDTO.FinancialOverview> getFinancialOverview() {
        return Result.success(adviceService.getFinancialOverview());
    }

    /**
     * 获取财务建议
     * @param request 查询参数
     * @return 建议列表及统计信息
     */
    @GetMapping
    public Result<AdviceDTO.AdviceResponse> getAdvices(AdviceDTO.QueryRequest request) {
        return Result.success(adviceService.queryAdvices(request));
    }

    /**
     * 将建议标记为已读
     * @param id 建议ID
     * @return 成功响应
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(@PathVariable Long id) {
        adviceService.markAsRead(id);
        return Result.success();
    }

    /**
     * 将所有建议标记为已读
     * @return 成功响应
     */
    @PutMapping("/read-all")
    public Result<Void> markAllAsRead() {
        adviceService.markAllAsRead();
        return Result.success();
    }

    /**
     * 删除建议
     * @param id 建议ID
     * @return 成功响应
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteAdvice(@PathVariable Long id) {
        adviceService.deleteAdvice(id);
        return Result.success();
    }

    /**
     * 手动触发生成建议
     * @return 成功响应
     */
    @PostMapping("/generate")
    public Result<Void> generateAdvices() {
        adviceService.generateAdvices();
        return Result.success();
    }
}