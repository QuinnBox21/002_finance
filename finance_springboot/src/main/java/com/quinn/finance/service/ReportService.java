package com.quinn.finance.service;

import com.quinn.finance.dto.ReportDTO.ReportResponse;

public interface ReportService {
    
    /**
     * Get report data for a date range
     * @param startMonth start month in format YYYY-MM
     * @param endMonth end month in format YYYY-MM
     * @return report data including trends, category breakdowns, and statistics
     */
    ReportResponse getReportData(String startMonth, String endMonth);
} 