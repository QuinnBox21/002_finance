package com.quinn.finance.controller;

import com.quinn.core.common.controller.BaseController;
import com.quinn.core.common.model.Result;
import com.quinn.finance.dto.DashboardDTO;
import com.quinn.finance.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Dashboard", description = "仪表盘相关接口")
@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController extends BaseController {

    private final DashboardService dashboardService;

    @Operation(summary = "获取仪表盘数据")
    @GetMapping
    public Result<DashboardDTO.Response> getDashboardData(@AuthenticationPrincipal UserDetails userDetails) throws Exception {
        return Result.success(dashboardService.getDashboardData(userDetails.getUsername()));
    }
}