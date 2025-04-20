<template>
  <div class="ghibli-dashboard">
    <div class="page-header">
      <h2>财务精灵的魔法仪表盘</h2>
      <div class="header-decoration"></div>
    </div>
    


    <!-- 收支趋势图 -->
    <div class="ghibli-card trend-card">
      <div class="card-header">
        <h3>收支趋势</h3>
        <el-radio-group v-model="trendTimeRange" size="small" class="ghibli-radio-group">
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
          <el-radio-button label="year">本年</el-radio-button>
        </el-radio-group>
      </div>
      
      <div class="trend-container">
        <div class="chart-wrapper">
          <div ref="trendChartRef" class="trend-chart fixed-chart" />
        </div>
        <div class="floating-leaves">
          <div class="leaf leaf-1"></div>
          <div class="leaf leaf-2"></div>
          <div class="leaf leaf-3"></div>
        </div>
      </div>
    </div>
    <el-row :gutter="20" >
      <!-- 预算使用情况卡片 -->
      <el-col :xs="24" :sm="24" :md="8">
        <div class="ghibli-card budget-card">
          <div class="card-header">
            <h3>本月预算使用情况</h3>
            <div class="forest-spirit"></div>
          </div>

          <div class="budget-progress-container">
            <el-progress
                type="dashboard"
                :percentage="budgetUsage"
                :color="budgetProgressColor"
                :stroke-width="10"
            >
              <template #default="{ percentage }">
                <div class="progress-content">
                  <div class="progress-label">{{ percentage }}%</div>
                  <!--                  <div class="progress-info">
                                      <div>支出：{{ formatAmount(monthExpense) }}</div>
                                      <div>预算：{{ formatAmount(monthBudget) }}</div>
                                    </div>-->
                </div>
              </template>
            </el-progress>

            <div class="budget-message">
              {{ getBudgetMessage(budgetUsage) }}
            </div>
          </div>
        </div>
      </el-col>

      <!-- 收支统计卡片 -->
      <el-col :xs="24" :sm="24" :md="8">
        <div class="ghibli-card stats-card">
          <div class="card-header">
            <h3>本月收支统计</h3>
            <div class="kodama"></div>
          </div>

          <div class="stats-content">
            <div class="stats-item income">
              <div class="stats-icon income-icon"></div>
              <div class="stats-details">
                <div class="stats-label">收入</div>
                <div class="stats-amount">{{ formatAmount(monthIncome) }}</div>
                <div class="stats-trend" :class="{ 'up': monthIncomeGrowth > 0 }">
                  {{ monthIncomeGrowth > 0 ? '+' : '' }}{{ monthIncomeGrowth }}%
                  <span class="trend-arrow"></span>
                </div>
              </div>
            </div>

            <div class="stats-divider"></div>

            <div class="stats-item expense">
              <div class="stats-icon expense-icon"></div>
              <div class="stats-details">
                <div class="stats-label">支出</div>
                <div class="stats-amount">{{ formatAmount(monthExpense) }}</div>
                <div class="stats-trend" :class="{ 'down': monthExpenseGrowth < 0 }">
                  {{ monthExpenseGrowth > 0 ? '+' : '' }}{{ monthExpenseGrowth }}%
                  <span class="trend-arrow"></span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-col>

      <!-- 收支类别卡片 -->
      <el-col :xs="24" :sm="24" :md="8">
        <div class="ghibli-card category-card">
          <div class="card-header">
            <h3>支出类别 Top 5</h3>
            <div class="soot-sprite"></div>
          </div>

          <div class="category-list">
            <div v-for="(category, index) in topCategories" :key="index" class="category-item">
              <div class="category-info">
                <span class="category-name">{{ category.name }}</span>
                <span class="category-amount">{{ formatAmount(category.amount) }}</span>
              </div>
              <div class="category-progress-container">
                <div class="category-progress-bg"></div>
                <div
                    class="category-progress-fill"
                    :style="{ width: `${category.percentage}%`, backgroundColor: getCategoryColor(index) }"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { getDashboardData } from '../api/dashboard'
import dayjs from 'dayjs'

// 状态数据
const monthBudget = ref(5000)
const monthExpense = ref(3200)
const monthIncome = ref(6000)
const monthIncomeGrowth = ref(12.5)
const monthExpenseGrowth = ref(-5.2)
const trendTimeRange = ref('month')
const trendChartRef = ref<HTMLElement>()
let trendChart: echarts.ECharts | null = null

// 计算预算使用比例
const budgetUsage = computed(() => {
  return monthBudget.value === 0 ? 0 : Math.round((monthExpense.value / monthBudget.value) * 100);
})

// 预算进度条颜色
const budgetProgressColor = computed(() => {
  const usage = budgetUsage.value
  if (usage < 70) return '#7ca982'
  if (usage < 90) return '#e6a974'
  return '#e87461'
})

// 获取预算使用提示信息
const getBudgetMessage = (usage: number) => {
  if (usage < 50) return '预算充足，继续保持！'
  if (usage < 70) return '预算使用良好，注意节约！'
  if (usage < 90) return '预算即将用完，请谨慎消费！'
  if (usage < 100) return '预算已基本用完，请控制支出！'
  return '预算已超支，请立即节约！'
}

// Top 5支出类别
const topCategories = ref([
  { name: '餐饮', amount: 1200, percentage: 37.5 },
  { name: '交通', amount: 800, percentage: 25 },
  { name: '购物', amount: 600, percentage: 18.75 },
  { name: '娱乐', amount: 400, percentage: 12.5 },
  { name: '其他', amount: 200, percentage: 6.25 }
])

// 类别颜色
const getCategoryColor = (index: number) => {
  const colors = ['#7ca982', '#94b0da', '#e6a974', '#e87461', '#b38e8e'];
  return colors[index % colors.length];
}

// 格式化金额
const formatAmount = (amount: number) => {
  return `¥${amount.toFixed(2)}`
}

// 初始化趋势图
const initTrendChart = () => {
  if (!trendChartRef.value) return
  
  // 使用固定尺寸初始化图表，避免自动调整大小
  trendChart = echarts.init(trendChartRef.value, null, {
    renderer: 'canvas',
    width: trendChartRef.value.offsetWidth,
    height: trendChartRef.value.offsetHeight
  })
  
  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a0}: {c0}<br/>{a1}: {c1}',
      backgroundColor: 'rgba(255, 255, 255, 0.8)',
      borderColor: '#a1c181',
      borderWidth: 1,
      textStyle: {
        color: '#5d534a'
      }
    },
    legend: {
      data: ['收入', '支出'],
      top: 0,
      textStyle: {
        color: '#5d534a'
      },
      itemStyle: {
        borderWidth: 0
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
      axisLine: {
        lineStyle: {
          color: '#d0cdc7'
        }
      },
      axisLabel: {
        color: '#5d534a'
      }
    },
    yAxis: {
      type: 'value',
      axisLine: {
        lineStyle: {
          color: '#d0cdc7'
        }
      },
      splitLine: {
        lineStyle: {
          type: 'dashed',
          color: '#e8e5df'
        }
      },
      axisLabel: {
        color: '#5d534a'
      }
    },
    series: [
      {
        name: '收入',
        type: 'line',
        smooth: true,
        data: [0, 0, 0, 0, 0, 0, 0],
        itemStyle: {
          color: '#7ca982'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(124, 169, 130, 0.3)' },
            { offset: 1, color: 'rgba(124, 169, 130, 0.1)' }
          ])
        }
      },
      {
        name: '支出',
        type: 'line',
        smooth: true,
        data: [0, 0, 0, 0, 0, 0, 0],
        itemStyle: {
          color: '#e87461'
        },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(232, 116, 97, 0.3)' },
            { offset: 1, color: 'rgba(232, 116, 97, 0.1)' }
          ])
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

// 获取仪表盘数据
const fetchDashboardData = async () => {
  try {
    const { budget, expense, income, incomeGrowth, expenseGrowth, categories } = await getDashboardData()
    
    monthBudget.value = budget
    monthExpense.value = expense
    monthIncome.value = income
    monthIncomeGrowth.value = incomeGrowth
    monthExpenseGrowth.value = expenseGrowth
    topCategories.value = categories
  } catch (error) {
    console.error('获取仪表盘数据失败:', error)
  }
}

// 使用简单的窗口resize事件代替ResizeObserver
const handleWindowResize = () => {
  if (trendChart && trendChartRef.value) {
    trendChart.resize({
      width: trendChartRef.value.offsetWidth,
      height: trendChartRef.value.offsetHeight
    })
  }
}

// 添加简单的防抖函数
function debounce(fn: Function, delay: number) {
  let timer: number | null = null
  return function(this: any, ...args: any[]) {
    if (timer) {
      clearTimeout(timer)
    }
    timer = window.setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 防抖的resize处理函数
const debouncedResize = debounce(handleWindowResize, 100)

onMounted(() => {
  fetchDashboardData()
  initTrendChart()
  
  // 使用窗口resize事件替代ResizeObserver
  window.addEventListener('resize', debouncedResize)
})

onUnmounted(() => {
  // 清理resize事件监听器
  window.removeEventListener('resize', debouncedResize)
  
  trendChart?.dispose()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;500;700&display=swap');

.ghibli-dashboard {
  position: relative;
  z-index: 1;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 30px;
  position: relative;
}

.page-header h2 {
  margin: 0 0 10px 0;
  color: #5a7247;
  font-size: 2rem;
  font-weight: 500;
  text-align: center;
}

.header-decoration {
  width: 200px;
  height: 20px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 800 80"><path fill="none" stroke="%23a1c181" stroke-width="6" stroke-linecap="round" d="M10,40 C80,10 160,70 240,40 C320,10 400,70 480,40 C560,10 640,70 720,40 C760,25 780,50 790,60"/></svg>');
  background-repeat: no-repeat;
  background-position: center;
  background-size: contain;
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
  height: 100%;
}

.ghibli-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #f3f0e9;
  padding-bottom: 15px;
}

.card-header h3 {
  margin: 0;
  color: #5a7247;
  font-size: 1.2rem;
  font-weight: 500;
}

.forest-spirit {
  width: 35px;
  height: 35px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%235a7247" d="M12,3c0,0-3-1-3,2s-5,3-5,7c0,3.5,1.5,5,3,5c0,0,0,2,2,2s2-2,2-2s0,2,2,2s2-2,2-2c1.5,0,3-1.5,3-5c0-4-5-4-5-7S12,3,12,3z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.7;
}

.kodama {
  width: 32px;
  height: 32px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><circle cx="12" cy="12" r="7" fill="%235a7247" fill-opacity="0.7"/><circle cx="9" cy="10" r="1.5" fill="%23333"/><circle cx="15" cy="10" r="1.5" fill="%23333"/><path d="M9,15 Q12,18 15,15" stroke="%23333" fill="none" stroke-width="0.8"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.soot-sprite {
  width: 32px;
  height: 32px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><circle cx="12" cy="12" r="8" fill="%23333" fill-opacity="0.7"/><circle cx="10" cy="10" r="1.2" fill="%23fff"/><circle cx="14" cy="10" r="1.2" fill="%23fff"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

/* 预算进度卡片 */
.budget-progress-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.progress-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.progress-label {
  font-size: 2rem;
  font-weight: 500;
  color: #5a7247;
  margin-bottom: 10px;
}

.progress-info {
  font-size: 0.9rem;
  color: #8c8c8c;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.budget-message {
  margin-top: 20px;
  padding: 10px 15px;
  background-color: #f8f6f0;
  border-radius: 20px;
  font-size: 0.9rem;
  color: #5a7247;
  text-align: center;
  border-left: 3px solid #a1c181;
}

/* 收支统计卡片 */
.stats-content {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  padding: 10px 0;
  height: 180px;
}

.stats-divider {
  width: 2px;
  background: linear-gradient(to bottom, transparent, #e8e5df, transparent);
  margin: 0 20px;
}

.stats-item {
  display: flex;
  flex: 1;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  padding: 15px;
}

.stats-icon {
  width: 50px;
  height: 50px;
  background-size: contain;
  background-repeat: no-repeat;
  margin-bottom: 15px;
}

.income-icon {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%237ca982" d="M12,6.5A2.5,2.5 0 0,1 14.5,9A2.5,2.5 0 0,1 12,11.5A2.5,2.5 0 0,1 9.5,9A2.5,2.5 0 0,1 12,6.5M12,2A7,7 0 0,1 19,9C19,11.38 17.81,13.47 16,14.74V17A1,1 0 0,1 15,18H9A1,1 0 0,1 8,17V14.74C6.19,13.47 5,11.38 5,9A7,7 0 0,1 12,2M9,21V20H15V21A1,1 0 0,1 14,22H10A1,1 0 0,1 9,21Z" /></svg>');
}

.expense-icon {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23e87461" d="M12,3C7.58,3 4,4.79 4,7C4,9.21 7.58,11 12,11C16.42,11 20,9.21 20,7C20,4.79 16.42,3 12,3M4,9V12C4,14.21 7.58,16 12,16C16.42,16 20,14.21 20,12V9C20,11.21 16.42,13 12,13C7.58,13 4,11.21 4,9M4,14V17C4,19.21 7.58,21 12,21C16.42,21 20,19.21 20,17V14C20,16.21 16.42,18 12,18C7.58,18 4,16.21 4,14Z" /></svg>');
}

.stats-details {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stats-label {
  font-size: 1rem;
  color: #8c8c8c;
  margin-bottom: 5px;
}

.stats-amount {
  font-size: 1.5rem;
  font-weight: 500;
  margin-bottom: 5px;
}

.income .stats-amount {
  color: #7ca982;
}

.expense .stats-amount {
  color: #e87461;
}

.stats-trend {
  font-size: 0.9rem;
  color: #8c8c8c;
  display: flex;
  align-items: center;
  gap: 5px;
}

.trend-arrow {
  display: inline-block;
  width: 12px;
  height: 12px;
  background-size: contain;
  background-repeat: no-repeat;
}

.stats-trend.up {
  color: #7ca982;
}

.stats-trend.up .trend-arrow {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%237ca982" d="M7,15L12,10L17,15H7Z" /></svg>');
}

.stats-trend.down {
  color: #e87461;
}

.stats-trend.down .trend-arrow {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23e87461" d="M7,10L12,15L17,10H7Z" /></svg>');
}

/* 类别卡片 */
.category-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.category-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.category-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-name {
  font-size: 0.9rem;
  color: #5d534a;
}

.category-amount {
  font-size: 0.9rem;
  color: #8c8c8c;
}

.category-progress-container {
  position: relative;
  height: 8px;
  border-radius: 4px;
  overflow: hidden;
}

.category-progress-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f1f1f1;
}

.category-progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
}

/* 趋势图卡片 */
.trend-container {
  position: relative;
  height: 300px;
  overflow: hidden;
}

.trend-chart {
  height: 100%;
  width: 100%;
  z-index: 1;
  position: relative;
}

.floating-leaves {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 2;
}

.leaf {
  position: absolute;
  width: 20px;
  height: 20px;
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.5;
}

.leaf-1 {
  top: 20%;
  left: 10%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23a1c181" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: floatLeaf 30s infinite linear;
}

.leaf-2 {
  top: 70%;
  right: 15%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%237ca982" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: floatLeaf 25s infinite linear reverse;
}

.leaf-3 {
  top: 40%;
  right: 30%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%2394b0da" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: floatLeaf 35s infinite linear;
}

@keyframes floatLeaf {
  0% { transform: translate(0, 0) rotate(0deg); }
  25% { transform: translate(10px, 10px) rotate(90deg); }
  50% { transform: translate(0, 20px) rotate(180deg); }
  75% { transform: translate(-10px, 10px) rotate(270deg); }
  100% { transform: translate(0, 0) rotate(360deg); }
}

/* 吉卜力风格的单选按钮组 */
.ghibli-radio-group {
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  border: none !important;
}

.ghibli-radio-group :deep(.el-radio-button__inner) {
  background-color: #f8f6f0 !important;
  color: #5d534a !important;
  border: none !important;
  box-shadow: none !important;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

.ghibli-radio-group :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #a1c181 !important;
  color: white !important;
  box-shadow: none !important;
}

@media screen and (max-width: 768px) {
  .ghibli-dashboard {
    padding: 20px 15px;
  }
  
  .stats-content {
    flex-direction: column;
    height: auto;
  }
  
  .stats-divider {
    width: 100%;
    height: 2px;
    margin: 20px 0;
  }
  
  .trend-container {
    height: 250px;
  }
}

/* 新增固定尺寸图表容器样式 */
.chart-wrapper {
  position: relative;
  width: 100%;
  height: 300px;
  overflow: hidden;
}

.fixed-chart {
  position: absolute;
  top: 0;
  left: 0;
  width: 100% !important;
  height: 100% !important;
}
</style>