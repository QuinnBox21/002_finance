import axios from '../utils/axios'

export interface Transaction {
  id: string
  type: 0 | 1 // 0: 支出, 1: 收入
  category: string
  amount: number
  date: string
  remark: string
}

export interface TransactionListResponse {
  list: never[]
  total: number
}

export interface TransactionFilter {
  page: number
  size: number
  type?: number
  category?: string
  startDate?: string
  endDate?: string
}

export const getTransactions = async (filter: TransactionFilter): Promise<TransactionListResponse> => {
  try {
    const response = await axios.get('/transactions', { params: filter })
    return response.data
  } catch (error) {
    console.error('获取交易记录失败:', error)
    throw error
  }
}

export const createTransaction = async (data: Omit<Transaction, 'id'>): Promise<Transaction> => {
  try {
    const response = await axios.post('/transactions', data)
    return response.data
  } catch (error) {
    console.error('创建交易记录失败:', error)
    throw error
  }
}

export const updateTransaction = async (id: string, data: Partial<Transaction>): Promise<Transaction> => {
  try {
    const response = await axios.put(`/transactions/${id}`, data)
    return response.data
  } catch (error) {
    console.error('更新交易记录失败:', error)
    throw error
  }
}

export const deleteTransaction = async (id: string): Promise<void> => {
  try {
    await axios.delete(`/transactions/${id}`)
  } catch (error) {
    console.error('删除交易记录失败:', error)
    throw error
  }
} 