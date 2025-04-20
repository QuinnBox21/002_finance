package com.quinn.finance.controller;

import com.quinn.core.common.controller.BaseController;
import com.quinn.core.common.model.Result;
import com.quinn.finance.dto.ReportDTO.ReportResponse;
import com.quinn.finance.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController extends BaseController {

    private final ReportService reportService;

    /**
     * Get report data for the specified period
     * @param startMonth Start month in format YYYY-MM
     * @param endMonth End month in format YYYY-MM
     * @return Report data including trend, categories and statistics
     */
    @GetMapping("/data")
    public Result<ReportResponse> getReportData(
            @RequestParam String startMonth,
            @RequestParam String endMonth) {
        return Result.success(reportService.getReportData(startMonth, endMonth));
    }
} 