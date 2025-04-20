import axios from '../utils/axios'

export interface DashboardData {
  budget: number
  expense: number
  income: number
  incomeGrowth: number
  expenseGrowth: number
  categories: Array<{
    name: string
    amount: number
    percentage: number
  }>
}

export const getDashboardData = async (): Promise<DashboardData> => {
  try {
    const response = await axios.get('/dashboard')
    return response.data
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
    throw error
  }
} 