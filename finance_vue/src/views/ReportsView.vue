<template>
  <div class="ghibli-reports">
    <div class="reports-background">
      <div class="forest-trees"></div>
      <div class="clouds">
        <div class="cloud cloud-1"></div>
        <div class="cloud cloud-2"></div>
        <div class="cloud cloud-3"></div>
      </div>
      <div class="falling-leaves">
        <div class="leaf leaf-1"></div>
        <div class="leaf leaf-2"></div>
        <div class="leaf leaf-3"></div>
        <div class="leaf leaf-4"></div>
        <div class="leaf leaf-5"></div>
      </div>
    </div>

    <div class="reports-container">
      <div class="page-header">
        <h2>魔法森林账本</h2>
        <div class="date-picker-wrapper">
          <div class="date-picker-decoration left"></div>
          <el-date-picker
              v-model="dateRange"
              type="monthrange"
              range-separator="至"
              start-placeholder="开始月份"
              end-placeholder="结束月份"
              value-format="YYYY-MM"
              @change="handleDateRangeChange"
              class="ghibli-date-picker"
          />
          <div class="date-picker-decoration right"></div>
        </div>
      </div>

      <!-- 收支趋势 -->
      <el-card class="chart-card ghibli-card">
        <template #header>
          <div class="card-header">
            <span>森林财富流动</span>
            <div class="green-leaf"></div>
            <el-radio-group v-model="trendType" size="small" @change="updateTrendChart" class="ghibli-radio">
              <el-radio-button label="amount">金额</el-radio-button>
              <el-radio-button label="count">笔数</el-radio-button>
            </el-radio-group>
          </div>
        </template>
        <div class="chart-wrapper">
          <div ref="trendChartRef" class="chart fixed-chart" />
        </div>
      </el-card>

      <!-- 收支构成 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="chart-card ghibli-card">
            <template #header>
              <div class="card-header">
                <span>魔法支出</span>
                <div class="green-leaf"></div>
              </div>
            </template>
            <div class="chart-wrapper">
              <div ref="expensePieRef" class="chart fixed-chart" />
            </div>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card class="chart-card ghibli-card">
            <template #header>
              <div class="card-header">
                <span>魔法收入</span>
                <div class="green-leaf"></div>
              </div>
            </template>
            <div class="chart-wrapper">
              <div ref="incomePieRef" class="chart fixed-chart" />
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 统计数据 -->
      <el-card class="ghibli-card">
        <template #header>
          <div class="card-header">
            <span>森林账本记录</span>
            <div class="green-leaf"></div>
          </div>
        </template>

        <el-table :data="statisticsData" border style="width: 100%" class="ghibli-table">
          <el-table-column prop="month" label="月份" width="120">
            <template #default="{ row }">
              {{ formatMonth(row.month) }}
            </template>
          </el-table-column>
          <el-table-column label="收入" width="200">
            <template #default="{ row }">
              <div>金额：{{ formatAmount(row.income) }}</div>
              <div class="small-text">笔数：{{ row.incomeCount }}笔</div>
            </template>
          </el-table-column>
          <el-table-column label="支出" width="200">
            <template #default="{ row }">
              <div>金额：{{ formatAmount(row.expense) }}</div>
              <div class="small-text">笔数：{{ row.expenseCount }}笔</div>
            </template>
          </el-table-column>
          <el-table-column label="结余" width="200">
            <template #default="{ row }">
              <div :class="{ 'income': row.balance >= 0, 'expense': row.balance < 0 }">
                {{ formatAmount(row.balance) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="预算使用率">
            <template #default="{ row }">
              <el-progress
                :percentage="row.budgetUsage"
                :status="row.budgetUsage > 100 ? 'exception' : ''"
                class="ghibli-progress"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>
    
    <div class="kodama-container">
      <div class="kodama kodama-1"></div>
      <div class="kodama kodama-2"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { getReportData, type ReportData, type StatisticsData } from '../api/report'
import dayjs from 'dayjs'

// 图表实例
let trendChart: echarts.ECharts | null = null
let expensePieChart: echarts.ECharts | null = null
let incomePieChart: echarts.ECharts | null = null

// DOM引用
const trendChartRef = ref<HTMLElement>()
const expensePieRef = ref<HTMLElement>()
const incomePieRef = ref<HTMLElement>()

// 状态数据
const dateRange = ref([dayjs().subtract(5, 'month').format('YYYY-MM'), dayjs().format('YYYY-MM')])
const trendType = ref('amount')
const statisticsData = ref<StatisticsData[]>([])

// 格式化金额
const formatAmount = (amount: number) => {
  return `¥${amount.toFixed(2)}`
}

// 格式化月份
const formatMonth = (month: string) => {
  if (!month) return ''
  const [year, monthNum] = month.split('-')
  return `${year}年${monthNum}月`
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
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['收入', '支出'],
      top: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '收入',
        type: 'bar',
        stack: 'total',
        emphasis: {
          focus: 'series'
        },
        itemStyle: {
          color: '#67c23a'
        },
        data: []
      },
      {
        name: '支出',
        type: 'bar',
        stack: 'total',
        emphasis: {
          focus: 'series'
        },
        itemStyle: {
          color: '#f56c6c'
        },
        data: []
      }
    ]
  }
  
  trendChart.setOption(option)
}

// 初始化饼图
const initPieCharts = () => {
  if (!expensePieRef.value || !incomePieRef.value) return
  
  // 支出饼图 - 使用固定尺寸
  expensePieChart = echarts.init(expensePieRef.value, null, {
    renderer: 'canvas',
    width: expensePieRef.value.offsetWidth,
    height: expensePieRef.value.offsetHeight
  })
  
  const expenseOption: EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '支出构成',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: []
      }
    ]
  }
  expensePieChart.setOption(expenseOption)

  // 收入饼图 - 使用固定尺寸
  incomePieChart = echarts.init(incomePieRef.value, null, {
    renderer: 'canvas',
    width: incomePieRef.value.offsetWidth,
    height: incomePieRef.value.offsetHeight
  })
  
  const incomeOption: EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '收入构成',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        data: []
      }
    ]
  }
  incomePieChart.setOption(incomeOption)
}

// 更新图表数据
const updateCharts = async () => {
  if (!dateRange.value) return
  
  try {
    const data = await getReportData(dateRange.value[0], dateRange.value[1])
    statisticsData.value = data.statistics

    // 格式化月份显示
    const formattedMonths = data.trend.months.map(month => {
      const [year, monthNum] = month.split('-')
      return `${monthNum}月`
    })

    // 更新趋势图
    if (trendChart) {
      const option: EChartsOption = {
        xAxis: {
          data: formattedMonths
        },
        series: [
          {
            name: '收入',
            data: trendType.value === 'amount' ? data.trend.income : data.trend.incomeCount
          },
          {
            name: '支出',
            data: trendType.value === 'amount' ? data.trend.expense : data.trend.expenseCount
          }
        ]
      }
      trendChart.setOption(option)
    }

    // 更新支出饼图
    if (expensePieChart) {
      const option: EChartsOption = {
        series: [{
          data: data.expenseCategories
        }]
      }
      expensePieChart.setOption(option)
    }

    // 更新收入饼图
    if (incomePieChart) {
      const option: EChartsOption = {
        series: [{
          data: data.incomeCategories
        }]
      }
      incomePieChart.setOption(option)
    }
  } catch (error) {
    console.error('更新图表数据失败:', error)
  }
}

// 处理日期范围变化
const handleDateRangeChange = () => {
  updateCharts()
}

// 更新趋势图类型
const updateTrendChart = () => {
  updateCharts()
}

// 使用简单的窗口resize事件代替ResizeObserver
const handleWindowResize = () => {
  if (trendChart && trendChartRef.value) {
    trendChart.resize({
      width: trendChartRef.value.offsetWidth,
      height: trendChartRef.value.offsetHeight
    })
  }
  if (expensePieChart && expensePieRef.value) {
    expensePieChart.resize({
      width: expensePieRef.value.offsetWidth,
      height: expensePieRef.value.offsetHeight
    })
  }
  if (incomePieChart && incomePieRef.value) {
    incomePieChart.resize({
      width: incomePieRef.value.offsetWidth,
      height: incomePieRef.value.offsetHeight
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
  initTrendChart()
  initPieCharts()
  updateCharts()
  
  // 使用窗口resize事件替代ResizeObserver
  window.addEventListener('resize', debouncedResize)
})

onUnmounted(() => {
  // 清理resize事件监听器
  window.removeEventListener('resize', debouncedResize)
  
  trendChart?.dispose()
  expensePieChart?.dispose()
  incomePieChart?.dispose()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;500;700&display=swap');

.ghibli-reports {
  min-height: 100vh;
  position: relative;
  overflow-x: hidden;
  font-family: 'Noto Serif SC', serif;
}

/* 新增固定尺寸图表容器样式 */
.chart-wrapper {
  position: relative;
  width: 100%;
  height: 350px;
  overflow: hidden;
}

.fixed-chart {
  position: absolute;
  top: 0;
  left: 0;
  width: 100% !important;
  height: 100% !important;
}

/* 背景元素 */
.reports-background {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
}

.forest-trees {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 20%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 200"><path fill="%235a7247" fill-opacity="0.7" d="M0,200 L0,100 Q50,80 100,100 Q150,120 200,100 Q250,80 300,100 Q350,120 400,100 Q450,80 500,100 Q550,120 600,100 Q650,80 700,100 Q750,120 800,100 Q850,80 900,100 Q950,120 1000,100 Q1050,80 1100,100 Q1150,120 1200,100 L1200,200 Z"/></svg>');
  background-size: cover;
  background-repeat: no-repeat;
  opacity: 0.3;
}

.clouds {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
}

.cloud-1 {
  top: 10%;
  left: 10%;
  width: 120px;
  height: 60px;
  animation: float 30s infinite linear;
}

.cloud-2 {
  top: 15%;
  right: 20%;
  width: 100px;
  height: 50px;
  animation: float 20s infinite linear reverse;
}

.cloud-3 {
  top: 8%;
  right: 10%;
  width: 80px;
  height: 40px;
  animation: float 25s infinite linear;
}

@keyframes float {
  0% { transform: translateX(0); }
  50% { transform: translateX(30px); }
  100% { transform: translateX(0); }
}

.falling-leaves {
  position: absolute;
  width: 100%;
  height: 100%;
}

.leaf {
  position: absolute;
  width: 20px;
  height: 20px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><path fill="%239cbc51" d="M50,0 C70,25 90,40 50,100 C10,40 30,25 50,0"/></svg>');
  background-size: contain;
  opacity: 0.3;
  animation: fall 10s infinite linear;
}

.leaf-1 { top: -20px; left: 10%; animation-duration: 12s; animation-delay: 0s; }
.leaf-2 { top: -20px; left: 30%; animation-duration: 10s; animation-delay: 2s; }
.leaf-3 { top: -20px; left: 50%; animation-duration: 15s; animation-delay: 4s; }
.leaf-4 { top: -20px; left: 70%; animation-duration: 11s; animation-delay: 1s; }
.leaf-5 { top: -20px; left: 90%; animation-duration: 13s; animation-delay: 3s; }

@keyframes fall {
  0% { transform: translateY(0) rotate(0deg); opacity: 0.6; }
  50% { transform: translateY(50vh) rotate(180deg); opacity: 0.4; }
  100% { transform: translateY(100vh) rotate(360deg); opacity: 0; }
}

.kodama-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 2;
}

.kodama {
  width: 40px;
  height: 60px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 150"><ellipse cx="50" cy="40" rx="40" ry="40" fill="white"/><ellipse cx="50" cy="120" rx="30" ry="30" fill="white"/><line x1="50" y1="80" x2="50" y2="90" stroke="white" stroke-width="20"/><circle cx="35" cy="35" r="5" fill="black"/><circle cx="65" cy="35" r="5" fill="black"/><ellipse cx="50" cy="50" rx="10" ry="5" fill="black"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.5;
  transition: opacity 0.3s;
}

.kodama:hover {
  opacity: 0.8;
}

.kodama-1 {
  transform: translateY(0px);
  animation: bob 3s infinite ease-in-out;
}

.kodama-2 {
  position: absolute;
  bottom: 20px;
  right: 50px;
  transform: translateY(0px) scale(0.7);
  animation: bob 3s infinite ease-in-out reverse;
}

@keyframes bob {
  0% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-10px) scale(1); }
  100% { transform: translateY(0) scale(1); }
}

/* 内容容器 */
.reports-container {
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
  margin-bottom: 20px;
}

.page-header h2 {
  color: #5a7247;
  font-weight: 500;
  margin: 0;
  position: relative;
}

.page-header h2:after {
  content: "";
  position: absolute;
  bottom: -5px;
  left: 0;
  width: 50px;
  height: 3px;
  background: #a1c181;
}

/* 卡片样式 */
.ghibli-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  background-color: rgba(255, 255, 255, 0.9);
}

.ghibli-card:hover {
  transform: translateY(-5px);
}

.chart-card {
  height: 430px;
}

.card-header {
  display: flex;
  align-items: center;
  position: relative;
}

.card-header span {
  font-weight: 500;
  color: #5a7247;
  font-size: 16px;
}

.green-leaf {
  width: 20px;
  height: 20px;
  margin-left: 10px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><path fill="%239cbc51" d="M50,0 C70,25 90,40 50,100 C10,40 30,25 50,0"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.chart {
  height: 350px;
}

/* 特定元素样式 */
.ghibli-date-picker {
  --el-border-radius-base: 10px;
}

.ghibli-radio :deep(.el-radio-button__inner) {
  border-color: #a1c181;
  background-color: transparent;
  color: #5a7247;
}

.ghibli-radio :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #a1c181;
  border-color: #a1c181;
  box-shadow: -1px 0 0 0 #a1c181;
  color: white;
}

.ghibli-table {
  --el-table-header-bg-color: rgba(161, 193, 129, 0.1);
  --el-table-border-color: rgba(161, 193, 129, 0.2);
  border-radius: 10px;
  overflow: hidden;
}

.ghibli-table :deep(th) {
  background-color: rgba(161, 193, 129, 0.2);
  color: #5a7247;
  font-weight: 500;
}

.ghibli-table :deep(td) {
  color: #606266;
}

.ghibli-progress :deep(.el-progress-bar__inner) {
  background-color: #a1c181;
}

.ghibli-progress :deep(.el-progress-bar__inner.is-exception) {
  background-color: #f56c6c;
}

.income {
  color: #67c23a;
  font-weight: 500;
}

.expense {
  color: #f56c6c;
  font-weight: 500;
}

.small-text {
  font-size: 12px;
  color: #909399;
}

/* 日期选择器样式增强 */
.date-picker-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.date-picker-decoration {
  width: 20px;
  height: 30px;
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.7;
}

.date-picker-decoration.left {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23a1c181" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  margin-right: 10px;
  transform: rotate(-45deg);
}

.date-picker-decoration.right {
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23a1c181" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  margin-left: 10px;
  transform: rotate(45deg);
}

.ghibli-date-picker {
  --el-border-radius-base: 20px;
}

.ghibli-date-picker :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.8) !important;
  box-shadow: 0 0 0 1px #a1c181 inset !important;
  border-radius: 20px !important;
  padding: 0 15px !important;
  transition: all 0.3s ease !important;
}

.ghibli-date-picker :deep(.el-input__wrapper:hover),
.ghibli-date-picker :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px #a1c181 inset !important;
  background-color: rgba(255, 255, 255, 0.95) !important;
  transform: translateY(-2px);
}

.ghibli-date-picker :deep(.el-input__inner) {
  color: #5a7247 !important;
  font-family: 'Noto Serif SC', serif !important;
}

.ghibli-date-picker :deep(.el-range-separator) {
  color: #a1c181 !important;
}

.ghibli-date-picker :deep(.el-input__prefix-inner) {
  color: #a1c181 !important;
}

/* Add animation for date picker icons */
@keyframes gentle-pulse {
  0% { transform: scale(1) rotate(-45deg); }
  50% { transform: scale(1.1) rotate(-45deg); }
  100% { transform: scale(1) rotate(-45deg); }
}

@keyframes gentle-pulse-right {
  0% { transform: scale(1) rotate(45deg); }
  50% { transform: scale(1.1) rotate(45deg); }
  100% { transform: scale(1) rotate(45deg); }
}

.date-picker-decoration.left {
  animation: gentle-pulse 3s infinite ease-in-out;
}

.date-picker-decoration.right {
  animation: gentle-pulse-right 3s infinite ease-in-out;
}
</style>