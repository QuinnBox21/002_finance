import axios from '../utils/axios'

export interface FinancialAdvice {
  id: number
  title: string
  content: string
  type: string // 'income', 'expense', 'budget', 'investment', 'other'
  priority: number // 1-5, 5 being highest
  createdAt: string
  read: boolean
}

export interface AdviceResponse {
  advices: FinancialAdvice[]
  total: number
  unreadCount: number
}

export interface FinancialOverview {
  averageIncome: number
  averageExpense: number
  incomeGrowth: number
  expenseGrowth: number
}

/**
 * 获取财务概览数据
 * @returns 财务概览数据，包括月均收支和同比增长率
 */
export async function getFinancialOverview(): Promise<FinancialOverview> {
  try {
    const response = await axios.get('/advices/overview')
    return response.data
  } catch (error) {
    console.error('获取财务概览失败:', error)
    throw error
  }
}

/**
 * 获取财务建议列表
 * @param page 页码
 * @param size 每页条数
 * @param type 建议类型
 * @returns 建议列表及统计信息
 */
export async function getAdvices(params: {
  page?: number
  size?: number
  type?: string
}): Promise<AdviceResponse> {
  try {
    const response = await axios.get('/advices', { params })
    return response.data
  } catch (error) {
    console.error('获取建议列表失败:', error)
    throw error
  }
}

/**
 * 标记建议为已读
 * @param id 建议ID
 */
export async function markAdviceAsRead(id: number): Promise<void> {
  try {
    await axios.put(`/advices/${id}/read`)
  } catch (error) {
    console.error('标记已读失败:', error)
    throw error
  }
}

/**
 * 标记所有建议为已读
 */
export async function markAllAdvicesAsRead(): Promise<void> {
  try {
    await axios.put('/advices/read-all')
  } catch (error) {
    console.error('全部标记已读失败:', error)
    throw error
  }
}

/**
 * 删除建议
 * @param id 建议ID
 */
export async function deleteAdvice(id: number): Promise<void> {
  try {
    await axios.delete(`/advices/${id}`)
  } catch (error) {
    console.error('删除建议失败:', error)
    throw error
  }
}

/**
 * 生成新建议
 */
export async function generateAdvice(): Promise<void> {
  try {
    await axios.post('/advices/generate')
  } catch (error) {
    console.error('生成建议失败:', error)
    throw error
  }
}