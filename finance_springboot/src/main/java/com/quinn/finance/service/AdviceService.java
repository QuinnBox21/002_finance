package com.quinn.finance.service;

import com.quinn.finance.dto.AdviceDTO;

public interface AdviceService {

    /**
     * 查询财务建议
     * @param request 查询参数
     * @return 建议列表及统计信息
     */
    AdviceDTO.AdviceResponse queryAdvices(AdviceDTO.QueryRequest request);

    /**
     * 将建议标记为已读
     * @param id 建议ID
     */
    void markAsRead(Long id);

    /**
     * 将所有建议标记为已读
     */
    void markAllAsRead();

    /**
     * 删除建议
     * @param id 建议ID
     */
    void deleteAdvice(Long id);

    /**
     * 生成财务建议
     * 系统会基于用户的财务数据自动生成建议
     */
    void generateAdvices();

    /**
     * 获取财务概览数据
     * @return 财务概览数据，包括月均收支和同比增长率
     */
    AdviceDTO.FinancialOverview getFinancialOverview();
}