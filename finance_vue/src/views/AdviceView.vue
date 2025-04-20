<template>
  <div class="ghibli-container">
    <div class="page-header">
      <h2>理财精灵的建议</h2>
      <el-button class="ghibli-button" @click="handleGenerateAdvice">
        <i class="el-icon-magic-stick"></i> 生成新建议
      </el-button>
    </div>

    <!-- 财务状况概览 -->
    <div class="overview-section">
      <div class="clouds">
        <div class="cloud cloud-1"></div>
        <div class="cloud cloud-2"></div>
        <div class="cloud cloud-3"></div>
      </div>
      
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8" :md="8">
          <div class="ghibli-card" v-loading="loading">
            <div class="card-header">
              <h3>月均收入</h3>
              <div class="leaf leaf-income"></div>
            </div>
            <div class="card-content">
              <div class="amount income">{{ formatAmount(averageIncome) }}</div>
              <div class="trend" :class="{ 'up': incomeGrowth > 0 }">
                较上月 {{ incomeGrowth > 0 ? '+' : '' }}{{ incomeGrowth }}%
                <i :class="incomeGrowth >= 0 ? 'el-icon-top' : 'el-icon-bottom'"></i>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="8" :md="8">
          <div class="ghibli-card" v-loading="loading">
            <div class="card-header">
              <h3>月均支出</h3>
              <div class="leaf leaf-expense"></div>
            </div>
            <div class="card-content">
              <div class="amount expense">{{ formatAmount(averageExpense) }}</div>
              <div class="trend" :class="{ 'down': expenseGrowth < 0 }">
                较上月 {{ expenseGrowth > 0 ? '+' : '' }}{{ expenseGrowth }}%
                <i :class="expenseGrowth <= 0 ? 'el-icon-bottom' : 'el-icon-top'"></i>
              </div>
            </div>
          </div>
        </el-col>

        <el-col :xs="24" :sm="8" :md="8">
          <div class="ghibli-card" v-loading="loading">
            <div class="card-header">
              <h3>月均结余</h3>
              <div class="leaf leaf-balance"></div>
            </div>
            <div class="card-content">
              <div class="amount" :class="{ 'income': monthlyBalance >= 0, 'expense': monthlyBalance < 0 }">
                {{ formatAmount(monthlyBalance) }}
              </div>
              <div class="saving-rate">
                收支比：{{ savingRate }}%
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: `${Math.min(Math.max(savingRate, 0), 100)}%` }"></div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 理财建议列表 -->
    <div class="advice-container" v-loading="listLoading">
      <div class="container-header">
        <h2>来自精灵的建议</h2>
        <div class="totoro-icon"></div>
      </div>

      <div class="advice-list">
        <div v-if="!adviceList || adviceList.length === 0" class="empty-state">
          <div class="soot-sprite"></div>
          <el-empty description="暂无理财建议，点击上方按钮生成" />
        </div>

        <transition-group name="advice-fade" tag="div" v-else>
          <div 
            v-for="advice in adviceList" 
            :key="advice.id" 
            class="advice-item"
            :class="{'advice-read': advice.read}"
          >
            <div class="advice-header">
              <div class="advice-type-tag" :class="'type-' + advice.type">
                {{ getTypeName(advice.type) }}
              </div>
              <div class="advice-date">{{ formatDate(advice.createdAt) }}</div>
            </div>
            
            <div class="advice-title">{{ advice.title }}</div>
            <div class="advice-content" v-html="formatAdviceContent(advice.content)" />
            
            <div class="advice-tail">
              <div class="advice-priority">
                优先级:
                <i 
                  v-for="n in 5" 
                  :key="n" 
                  class="priority-star" 
                  :class="{ 'active': n <= (advice.priority || 0) }"
                ></i>
              </div>
            </div>
          </div>
        </transition-group>
      </div>

      <div v-if="adviceList && adviceList.length > 0" class="pagination">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[5, 10, 20]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import { getFinancialOverview, getAdvices, generateAdvice, type FinancialAdvice } from '../api/advice'

// 加载状态
const loading = ref(false)
const listLoading = ref(false)

// 状态数据
const averageIncome = ref(0)
const averageExpense = ref(0)
const incomeGrowth = ref(0)
const expenseGrowth = ref(0)
const monthlyBalance = computed(() => averageIncome.value - averageExpense.value)
const savingRate = computed(() => {
  if (!averageIncome.value) return 0
  return Math.round((monthlyBalance.value / averageIncome.value) * 100)
})

// 分页数据
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const adviceList = ref<FinancialAdvice[]>([])

// 格式化金额
const formatAmount = (amount: number | undefined) => {
  if (amount === undefined || amount === null) {
    return '¥0.00'
  }
  return `¥${amount.toFixed(2)}`
}

// 格式化日期
const formatDate = (date: string | undefined) => {
  if (!date) return ''
  return dayjs(date).format('YYYY年MM月DD日 HH:mm')
}

// 获取时间线项目类型
const getTimelineItemType = (type: string | undefined): 'primary' | 'success' | 'warning' | 'danger' | 'info' => {
  if (!type) return 'info'
  
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'info'> = {
    'expense': 'warning',
    'income': 'success',
    'investment': 'primary',
    'budget': 'info'
  }
  return typeMap[type] || 'info'
}

// 获取建议标签类型
const getAdviceTagType = (type: string | undefined): 'primary' | 'success' | 'warning' | 'danger' | 'info' | undefined => {
  if (!type) return 'info'
  
  const typeMap: Record<string, 'primary' | 'success' | 'warning' | 'danger' | 'info' | undefined> = {
    'expense': 'danger',
    'income': 'success',
    'investment': undefined,
    'budget': 'warning'
  }
  return typeMap[type] || 'info'
}

// 格式化建议内容
const formatAdviceContent = (content: string | undefined) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

// 获取概览数据
const fetchOverviewData = async () => {
  loading.value = true
  try {
    const response = await getFinancialOverview()
    console.log(response)
    averageIncome.value = response.averageIncome || 0
    averageExpense.value = response.averageExpense || 0
    incomeGrowth.value = response.incomeGrowth || 0
    expenseGrowth.value = response.expenseGrowth || 0
  } catch (error) {
    console.error('获取概览数据失败:', error)
    ElMessage.error('获取财务概览失败')
  } finally {
    loading.value = false
  }
}

// 获取建议列表
const fetchAdviceList = async () => {
  listLoading.value = true
  try {
    const response = await getAdvices({
      page: currentPage.value,
      size: pageSize.value
    })
    
    adviceList.value = response.advices || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取建议列表失败:', error)
    ElMessage.error('获取建议列表失败')
    adviceList.value = []
    total.value = 0
  } finally {
    listLoading.value = false
  }
}

// 生成新建议
const handleGenerateAdvice = async () => {
  try {
    await generateAdvice()
    ElMessage.success('已生成新的理财建议')
    fetchAdviceList()
  } catch (error) {
    console.error('生成建议失败:', error)
    ElMessage.error('生成建议失败')
  }
}

// 获取类型名称
const getTypeName = (type: string | undefined): string => {
  if (!type) return '其他建议'
  
  const typeNameMap: Record<string, string> = {
    'expense': '支出建议',
    'income': '收入建议',
    'investment': '投资建议',
    'budget': '预算建议',
    'other': '其他建议'
  }
  
  return typeNameMap[type] || '其他建议'
}

// 处理分页
const handleSizeChange = (val: number) => {
  pageSize.value = val
  fetchAdviceList()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  fetchAdviceList()
}

onMounted(() => {
  fetchOverviewData()
  fetchAdviceList()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;500;700&display=swap');

.ghibli-container {
  position: relative;
  z-index: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  position: relative;
}

.page-header h2 {
  margin: 0;
  color: #5a7247;
  font-size: 2rem;
  font-weight: 500;
  position: relative;
}

.page-header h2::after {
  content: "";
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(to right, #a1c181, transparent);
}

.ghibli-button {
  background-color: #7ca982 !important;
  border: none !important;
  color: #fff !important;
  border-radius: 20px !important;
  padding: 12px 24px !important;
  transition: all 0.3s ease !important;
  font-family: 'Noto Serif SC', serif !important;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1) !important;
}

.ghibli-button:hover {
  transform: translateY(-2px);
  background-color: #659a6e !important;
  box-shadow: 0 6px 8px rgba(0, 0, 0, 0.15) !important;
}

.overview-section {
  position: relative;
  overflow: hidden;
}

.clouds {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
}

.cloud-1 {
  top: 20px;
  left: 10%;
  width: 100px;
  height: 60px;
  animation: float 30s infinite linear;
}

.cloud-2 {
  top: 50px;
  right: 15%;
  width: 80px;
  height: 50px;
  animation: float 20s infinite linear reverse;
}

.cloud-3 {
  bottom: 30px;
  left: 30%;
  width: 60px;
  height: 40px;
  animation: float 25s infinite linear;
}

@keyframes float {
  0% { transform: translateX(0) translateY(0); }
  25% { transform: translateX(20px) translateY(-10px); }
  50% { transform: translateX(0) translateY(0); }
  75% { transform: translateX(-20px) translateY(10px); }
  100% { transform: translateX(0) translateY(0); }
}

.ghibli-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.05);
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  min-height: 180px;
  z-index: 1;
}

.ghibli-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
}

.ghibli-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 8px;
  background: #a1c181;
  z-index: 2;
}

.ghibli-card:nth-child(2)::before {
  background: #f2a07b;
}

.ghibli-card:nth-child(3)::before {
  background: #94b0da;
}

.card-header {
  margin-bottom: 15px;
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  color: #5d534a;
  font-size: 1.2rem;
  font-weight: 500;
}

.leaf {
  width: 30px;
  height: 30px;
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.8;
}

.leaf-income {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23a1c181" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
}

.leaf-expense {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23f2a07b" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
}

.leaf-balance {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%2394b0da" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
}

.card-content {
  position: relative;
  z-index: 1;
}

.amount {
  font-size: 2rem;
  font-weight: 500;
  margin-bottom: 10px;
}

.income {
  color: #7ca982;
}

.expense {
  color: #e87461;
}

.trend {
  font-size: 0.9rem;
  color: #8c8c8c;
  display: flex;
  align-items: center;
  gap: 5px;
}

.trend.up {
  color: #7ca982;
}

.trend.down {
  color: #e87461;
}

.saving-rate {
  font-size: 0.9rem;
  color: #8c8c8c;
}

.progress-bar {
  height: 8px;
  background-color: #f1f1f1;
  border-radius: 4px;
  margin-top: 8px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: #94b0da;
  border-radius: 4px;
  transition: width 0.5s ease;
}

/* Advice Container */
.advice-container {
  background: #fff;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.05);
  position: relative;
}

.container-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  border-bottom: 2px solid #f8f6f0;
  padding-bottom: 15px;
}

.container-header h2 {
  margin: 0;
  color: #5a7247;
  font-size: 1.5rem;
  font-weight: 500;
}

.totoro-icon {
  width: 40px;
  height: 40px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path fill="%235d534a" d="M256,224c-79.41,0-192,122.76-192,200.25,0,34.9,26.81,55.75,71.74,55.75,48.84,0,81.61-25.08,120.26-25.08,38.47,0,72.31,24.34,120.26,24.34,43.44,0,71.74-20.22,71.74-55.75C448,346.16,335.23,224,256,224Z"/><path fill="%235d534a" d="M256,288s-32-32-32-64,32-96,32-96,32,64,32,96S256,288,256,288Z"/><path fill="%235d534a" d="M288,128s32,32,32,64S288,256,288,256s32-32,32-64S288,128,288,128Z"/><path fill="%235d534a" d="M224,128s-32,32-32,64,32,64,32,64-32-32-32-64S224,128,224,128Z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.advice-list {
  position: relative;
}

.advice-item {
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  background: #f8f6f0;
  position: relative;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border-left: 4px solid #a1c181;
}

.advice-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
}

.advice-item.advice-read {
  opacity: 0.7;
}

.advice-fade-enter-active, .advice-fade-leave-active {
  transition: all 0.5s ease;
}

.advice-fade-enter-from, .advice-fade-leave-to {
  opacity: 0;
  transform: translateY(30px);
}

.advice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.advice-type-tag {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  color: white;
  background-color: #a1c181;
}

.type-expense {
  background-color: #e87461;
  border-left-color: #e87461;
}

.type-income {
  background-color: #7ca982;
  border-left-color: #7ca982;
}

.type-investment {
  background-color: #94b0da;
  border-left-color: #94b0da;
}

.type-budget {
  background-color: #f2a07b;
  border-left-color: #f2a07b;
}

.advice-date {
  font-size: 0.8rem;
  color: #8c8c8c;
}

.advice-title {
  font-size: 1.2rem;
  font-weight: 500;
  margin-bottom: 10px;
  color: #5a7247;
}

.advice-content {
  margin-bottom: 15px;
  line-height: 1.6;
  color: #5d534a;
}

.advice-tail {
  display: flex;
  justify-content: flex-end;
}

.advice-priority {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 0.8rem;
  color: #8c8c8c;
}

.priority-star {
  display: inline-block;
  width: 16px;
  height: 16px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23d9d9d9" d="M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.priority-star.active {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23ffb957" d="M12,17.27L18.18,21L16.54,13.97L22,9.24L14.81,8.62L12,2L9.19,8.62L2,9.24L7.45,13.97L5.82,21L12,17.27Z"/></svg>');
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  text-align: center;
}

.soot-sprite {
  width: 80px;
  height: 80px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><circle cx="256" cy="256" r="208" fill="%23333333"/><circle cx="208" cy="208" r="32" fill="%23ffffff"/><circle cx="304" cy="208" r="32" fill="%23ffffff"/><circle cx="208" cy="208" r="16" fill="%23000000"/><circle cx="304" cy="208" r="16" fill="%23000000"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  margin-bottom: 20px;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}

@media screen and (max-width: 768px) {
  .ghibli-container {
    padding: 20px 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .page-header h2 {
    margin-bottom: 15px;
  }
  
  .ghibli-button {
    width: 100%;
    margin-top: 10px;
  }
  
  .advice-item {
    padding: 15px;
  }
  
  .advice-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .advice-date {
    margin-top: 5px;
  }
}
</style>