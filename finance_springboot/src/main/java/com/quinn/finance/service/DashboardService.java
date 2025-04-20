package com.quinn.finance.service;

import com.quinn.finance.dto.DashboardDTO;

public interface DashboardService {
    /**
     * 获取用户的仪表盘数据
     *
     * @param userId 用户ID
     * @return 仪表盘数据
     */
    DashboardDTO.Response getDashboardData(String userId) throws Exception;
}