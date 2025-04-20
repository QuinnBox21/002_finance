import axios from '@/utils/axios'

export interface TrendData {
  months: string[]
  income: number[]
  expense: number[]
  incomeCount: number[]
  expenseCount: number[]
}

export interface CategoryData {
  value: number
  name: string
}

export interface StatisticsData {
  month: string
  income: number
  expense: number
  balance: number
  budgetUsage: number
  incomeCount: number
  expenseCount: number
}

export interface ReportData {
  trend: TrendData
  expenseCategories: CategoryData[]
  incomeCategories: CategoryData[]
  statistics: StatisticsData[]
}

/**
 * Get report data for a date range
 * @param startMonth Start month in format YYYY-MM
 * @param endMonth End month in format YYYY-MM
 * @returns Report data including trends, categories and statistics
 */
export function getReportData(startMonth: string, endMonth: string): Promise<ReportData> {
  console.log('Fetching report data:', startMonth, endMonth);
  return axios({
    url: '/reports/data',
    method: 'get',
    params: {
      startMonth,
      endMonth
    }
  }).then(response => {
    console.log('Received report data:', response.data);
    return response.data;
  }).catch(error => {
    console.error('Error fetching report data:', error);
    throw error;
  });
}