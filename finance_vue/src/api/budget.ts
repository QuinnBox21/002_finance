import axios from '../utils/axios'

export interface BudgetItem {
  id: string
  category: string
  amount: number
  used: number
  remaining: number
  remark: string
}

export interface BudgetData {
  budgetList: never[]
  total: number,
  used: number
}

export const getBudgetList = async (month: string): Promise<BudgetData> => {
  try {
    const response = await axios.get('/budgets', { params: { month } })
    return response.data
  } catch (error) {
    console.error('获取预算列表失败:', error)
    throw error
  }
}


export const createBudget = async (data: Omit<BudgetItem, 'id' | 'used' | 'remaining'>): Promise<BudgetItem> => {
  try {
    const response = await axios.post('/budgets', data)
    return response.data
  } catch (error) {
    console.error('创建预算失败:', error)
    throw error
  }
}

export const updateBudget = async (id: string, data: Partial<BudgetItem>): Promise<BudgetItem> => {
  try {
    const response = await axios.put(`/budgets/${id}`, data)
    return response.data
  } catch (error) {
    console.error('更新预算失败:', error)
    throw error
  }
}

export const deleteBudget = async (id: string): Promise<void> => {
  try {
    await axios.delete(`/budgets/${id}`)
  } catch (error) {
    console.error('删除预算失败:', error)
    throw error
  }
} 