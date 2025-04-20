<template>
  <div class="ghibli-budget">
    <div class="page-header">
      <h2>森之宝藏守护者</h2>
      <el-button class="ghibli-button" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增预算
      </el-button>
    </div>
    
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="tree tree-left"></div>
      <div class="tree tree-right"></div>
      <div class="flying-element bird-1"></div>
      <div class="flying-element bird-2"></div>
    </div>

    <!-- 预算概览 -->
    <div class="overview-section chart-card" >
      <el-row :gutter="20">
        <el-col :xs="24" :sm="24" :md="8">
          <div class="ghibli-card">
            <div class="card-header">
              <h3>本月总预算</h3>
              <div class="acorn-icon"></div>
            </div>
            <div class="card-content">
              <div class="amount total">{{ formatAmount(monthlyTotal) }}</div>
              <div class="secondary-info">
                剩余：
                <span :class="{'positive': monthlyRemaining >= 0, 'negative': monthlyRemaining < 0}">
                  {{ formatAmount(monthlyRemaining) }}
                </span>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="8">
          <div class="ghibli-card">
            <div class="card-header">
              <h3>已用预算</h3>
              <div class="leaf-icon"></div>
            </div>
            <div class="card-content">
              <div class="amount expense">{{ formatAmount(monthlyUsed) }}</div>
              <div class="secondary-info">
                使用率：
                <span :class="{'warning': usageRate > 70, 'danger': usageRate > 90}">
                  {{ usageRate }}%
                </span>
              </div>
            </div>
          </div>
        </el-col>
        
        <el-col :xs="24" :sm="24" :md="8">
          <div class="ghibli-card">
            <div class="card-header">
              <h3>预算状态</h3>
              <div class="totoro-icon"></div>
            </div>
            <div class="card-content dashboard">
              <el-progress
                type="dashboard"
                :percentage="usageRate"
                :color="progressColor"
                :stroke-width="10"
              >
                <template #default="{ percentage }">
                  <div class="progress-content">
                    <div class="progress-label">{{ percentage }}%</div>
                    <div class="progress-status">{{ budgetStatus }}</div>
                  </div>
                </template>
              </el-progress>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 预算列表 -->
    <div class="chart-card ghibli-card budget-details">
      <div class="card-header">
        <h3>预算详情</h3>
        <el-date-picker
          v-model="currentMonth"
          type="month"
          placeholder="选择月份"
          format="YYYY年MM月"
          value-format="YYYY-MM"
          @change="handleMonthChange"
          class="ghibli-date-picker"
        />
      </div>

      <div class="table-wrapper" v-loading="loading">
        <el-table
          :data="budgetList"
          border
          class="ghibli-table"
        >
          <el-table-column prop="category" label="类别" width="120">
            <template #default="{ row }">
              <div class="category-tag">
                {{ row.category }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="amount" label="预算金额" width="120">
            <template #default="{ row }">
              <span class="budget-amount">{{ formatAmount(row.amount) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="used" label="已用金额" width="120">
            <template #default="{ row }">
              <span class="expense">{{ formatAmount(row.used) }}</span>
            </template>
          </el-table-column>
          
          <el-table-column prop="remaining" label="剩余金额" width="120">
            <template #default="{ row }">
              <span :class="{ 'positive': row.remaining >= 0, 'negative': row.remaining < 0 }">
                {{ formatAmount(row.remaining) }}
              </span>
            </template>
          </el-table-column>
          
          <el-table-column label="使用进度" min-width="180">
            <template #default="{ row }">
              <div class="progress-wrapper">
                <div class="progress-bg"></div>
                <div 
                  class="progress-fill" 
                  :class="{'warning': row.used/row.amount > 0.7, 'danger': row.used/row.amount > 0.9}"
                  :style="{width: `${Math.min(Math.round((row.used / row.amount) * 100), 100)}%`}"
                ></div>
                <span class="progress-text">{{ Math.min(Math.round((row.used / row.amount) * 100), 100) }}%</span>
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="remark" label="备注" min-width="150" />
          
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <div class="action-buttons">
                <el-button
                  class="edit-button"
                  @click="handleEdit(row)"
                >
                  编辑
                </el-button>
                <el-button
                  class="delete-button"
                  @click="handleDelete(row)"
                >
                  删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <div class="empty-state" v-if="budgetList.length === 0 && !loading">
        <div class="soot-sprite"></div>
        <p>还没有预算，点击上方按钮添加吧！</p>
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增预算' : '编辑预算'"
      width="500px"
      custom-class="ghibli-dialog"
    >
      <div class="dialog-decoration"></div>
      
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="ghibli-form"
      >
        <el-form-item label="类别" prop="category">
          <el-select
            v-model="form.category"
            placeholder="请选择类别"
            style="width: 100%"
            class="ghibli-select"
          >
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="金额" prop="amount">
          <el-input-number
            v-model="form.amount"
            :precision="2"
            :step="100"
            :min="0"
            style="width: 100%"
            class="ghibli-input-number"
          />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
            class="ghibli-textarea"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button class="ghibli-cancel-btn" @click="dialogVisible = false">取消</el-button>
          <el-button class="ghibli-confirm-btn" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getBudgetList, 
  createBudget, 
  updateBudget, 
  deleteBudget,
  type BudgetItem
} from '@/api/budget'
import dayjs from 'dayjs'

// 状态数据
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const formRef = ref<FormInstance>()
const currentMonth = ref(dayjs().format('YYYY-MM'))

// 预算概览数据
const monthlyTotal = ref(0)
const monthlyUsed = ref(0)
const monthlyRemaining = computed(() => monthlyTotal.value - monthlyUsed.value)
const usageRate = computed(() => Math.round((monthlyUsed.value / monthlyTotal.value) * 100) || 0)

// 进度条颜色
const progressColor = computed(() => {
  const rate = usageRate.value
  if (rate < 70) return '#7ca982'
  if (rate < 90) return '#e6a974'
  return '#e87461'
})

// 预算状态
const budgetStatus = computed(() => {
  const rate = usageRate.value
  if (rate < 70) return '预算充足'
  if (rate < 90) return '注意节约'
  return '即将超支'
})

// 表单数据
const form = ref({
  id: '',
  category: '',
  amount: 0,
  remark: ''
})

// 表单验证规则
const rules: FormRules = {
  category: [{ required: true, message: '请选择类别', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }]
}

// 类别选项
const categories = ref([
  '餐饮', '交通', '购物', '娱乐', '居住', '通讯', '医疗',
  '教育', '其他'
])

// 预算列表数据
const budgetList = ref<BudgetItem[]>([])

// 格式化金额
const formatAmount = (amount: number) => {
  return `¥${amount.toFixed(2)}`
}

// 获取预算列表
const fetchBudgetList = async () => {
  loading.value = true
  try {
    const data = await getBudgetList(currentMonth.value)
    budgetList.value = data.budgetList.list || []
    
    // 计算总预算和已用金额
    monthlyTotal.value = budgetList.value.reduce((total, item) => total + item.amount, 0)
    monthlyUsed.value = budgetList.value.reduce((total, item) => total + item.used, 0)
  } catch (error) {
    console.error('获取预算列表失败:', error)
    ElMessage.error('获取预算列表失败')
  } finally {
    loading.value = false
  }
}

// 月份变更
const handleMonthChange = () => {
  fetchBudgetList()
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await createBudget({
            ...form.value,
            month: currentMonth.value
          })
          ElMessage.success('新增预算成功')
        } else {
          await updateBudget(form.value.id, form.value)
          ElMessage.success('更新预算成功')
        }
        dialogVisible.value = false
        fetchBudgetList()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
      }
    }
  })
}

// 新增预算
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    id: '',
    category: '',
    amount: 0,
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑预算
const handleEdit = (row: BudgetItem) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除预算
const handleDelete = (row: BudgetItem) => {
  ElMessageBox.confirm('确定要删除该预算吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBudget(row.id)
      ElMessage.success('删除预算成功')
      fetchBudgetList()
    } catch (error) {
      console.error('删除预算失败:', error)
      ElMessage.error('删除预算失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  fetchBudgetList()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;500;700&display=swap');

.ghibli-budget_old {
  padding: 30px;
  background-color: #f8f6f0;
  min-height: 100vh;
  font-family: 'Noto Serif SC', serif;
  color: #5d534a;
  position: relative;
  overflow: hidden;
}

.ghibli-budget {
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
  z-index: 2;
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

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  pointer-events: none;
}

.tree {
  position: absolute;
  width: 80px;
  height: 160px;
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.1;
}

.tree-left {
  top: 10%;
  left: 5%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 200"><path fill="%235a7247" d="M50,0 C30,40 10,60 10,100 C10,140 30,160 50,160 C70,160 90,140 90,100 C90,60 70,40 50,0 Z"/><rect fill="%235d534a" x="45" y="160" width="10" height="40"/></svg>');
}

.tree-right {
  top: 30%;
  right: 5%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 200"><path fill="%235a7247" d="M50,0 C30,40 10,60 10,100 C10,140 30,160 50,160 C70,160 90,140 90,100 C90,60 70,40 50,0 Z"/><rect fill="%235d534a" x="45" y="160" width="10" height="40"/></svg>');
}

.flying-element {
  position: absolute;
  width: 20px;
  height: 20px;
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.2;
}

.bird-1 {
  top: 15%;
  left: 20%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%235a7247" d="M5,7L7,5H9.8L12.8,8H18V10H12.5L9.5,7H7.2L5,9H3V7H5M11,8V5L14.5,2L17,4.5L22,8L19,11L14,8L11,11V14H9V11L4,8L7,5L11,8Z"/></svg>');
  animation: fly 20s infinite linear;
}

.bird-2 {
  top: 30%;
  right: 25%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%235a7247" d="M5,7L7,5H9.8L12.8,8H18V10H12.5L9.5,7H7.2L5,9H3V7H5M11,8V5L14.5,2L17,4.5L22,8L19,11L14,8L11,11V14H9V11L4,8L7,5L11,8Z"/></svg>');
  animation: fly 25s infinite linear reverse;
}

@keyframes fly {
  0% { transform: translate(0, 0); }
  25% { transform: translate(50px, 25px); }
  50% { transform: translate(100px, 0); }
  75% { transform: translate(50px, -25px); }
  100% { transform: translate(0, 0); }
}

.overview-section {
  margin-bottom: 30px;
  position: relative;
  z-index: 2;
}

.ghibli-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.05);
  position: relative;
  transition: all 0.3s ease;
  height: 100%;
  z-index: 1;
}

.ghibli-card:hover {
  transform: translateY(-5px);
  overflow: hidden;
}

.chart-card {
  overflow: hidden;
  min-height: 280px;
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

.acorn-icon {
  width: 28px;
  height: 28px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%235a7247" fill-opacity="0.7" d="M12,2C8.13,2 5,5.13 5,9C5,14.5 12,22 12,22C12,22 19,14.5 19,9C19,5.13 15.87,2 12,2M12,11.5A2.5,2.5 0 0,1 9.5,9A2.5,2.5 0 0,1 12,6.5A2.5,2.5 0 0,1 14.5,9A2.5,2.5 0 0,1 12,11.5Z" /></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.leaf-icon {
  width: 28px;
  height: 28px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%235a7247" fill-opacity="0.7" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z" /></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.totoro-icon {
  width: 30px;
  height: 30px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path fill="%235d534a" d="M256,224c-79.41,0-192,122.76-192,200.25,0,34.9,26.81,55.75,71.74,55.75,48.84,0,81.61-25.08,120.26-25.08,38.47,0,72.31,24.34,120.26,24.34,43.44,0,71.74-20.22,71.74-55.75C448,346.16,335.23,224,256,224Z"/><path fill="%235d534a" d="M256,288s-32-32-32-64,32-96,32-96,32,64,32,96S256,288,256,288Z"/><path fill="%235d534a" d="M288,128s32,32,32,64S288,256,288,256s32-32,32-64S288,128,288,128Z"/><path fill="%235d534a" d="M224,128s-32,32-32,64,32,64,32,64-32-32-32-64S224,128,224,128Z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.8;
}

.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
}

.card-content.dashboard {
  padding: 0;
}

.amount {
  font-size: 2rem;
  font-weight: 500;
  margin-bottom: 10px;
}

.total {
  color: #7ca982;
}

.expense {
  color: #e87461;
}

.secondary-info {
  font-size: 0.9rem;
  color: #8c8c8c;
}

.positive {
  color: #7ca982;
}

.negative {
  color: #e87461;
}

.warning {
  color: #e6a974;
}

.danger {
  color: #e87461;
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
  margin-bottom: 5px;
}

.progress-status {
  font-size: 0.9rem;
  color: #8c8c8c;
}

/* 表格样式 */
.table-wrapper {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 15px;
}

.ghibli-table {
  border-color: #e8e5df !important;
  font-family: 'Noto Serif SC', serif;
}

.ghibli-table :deep(th.el-table__cell) {
  background-color: #f8f6f0 !important;
  color: #5a7247 !important;
  font-weight: 500;
  border-color: #e8e5df !important;
}

.ghibli-table :deep(td.el-table__cell) {
  border-color: #e8e5df !important;
}

.ghibli-table :deep(.el-table__row:hover > td.el-table__cell) {
  background-color: #f3f0e9 !important;
}

.category-tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 15px;
  background-color: #f3f0e9;
  color: #5d534a;
  font-size: 0.9rem;
}

.budget-amount {
  color: #7ca982;
  font-weight: 500;
}

.progress-wrapper {
  position: relative;
  height: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.progress-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #f3f0e9;
}

.progress-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  background-color: #7ca982;
  border-radius: 8px;
  transition: width 0.5s ease;
}

.progress-fill.warning {
  background-color: #e6a974;
}

.progress-fill.danger {
  background-color: #e87461;
}

.progress-text {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5d534a;
  font-size: 0.8rem;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.edit-button {
  color: #94b0da !important;
  border: none !important;
  background: none !important;
  padding: 0 !important;
  font-size: 0.9rem !important;
  transition: all 0.3s ease !important;
}

.edit-button:hover {
  color: #7a9ac8 !important;
  text-decoration: underline;
}

.delete-button {
  color: #e87461 !important;
  border: none !important;
  background: none !important;
  padding: 0 !important;
  font-size: 0.9rem !important;
  transition: all 0.3s ease !important;
}

.delete-button:hover {
  color: #d6624f !important;
  text-decoration: underline;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 50px 0;
  color: #8c8c8c;
}

.soot-sprite {
  width: 60px;
  height: 60px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10" fill="%23333" fill-opacity="0.6"/><circle cx="9" cy="10" r="1.5" fill="%23fff"/><circle cx="15" cy="10" r="1.5" fill="%23fff"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  margin-bottom: 15px;
}

/* 日期选择器样式 */
.ghibli-date-picker {
  width: 150px;
}

.ghibli-date-picker :deep(.el-input__wrapper) {
  background-color: #f8f6f0 !important;
  box-shadow: 0 0 0 1px #d0cdc7 inset !important;
  border-radius: 8px !important;
}

.ghibli-date-picker :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #a1c181 inset !important;
}

.ghibli-date-picker :deep(.el-input__inner) {
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif;
}

/* 对话框样式 */
.ghibli-dialog {
  border-radius: 12px !important;
  overflow: hidden !important;
  padding: 0 !important;
}

.ghibli-dialog :deep(.el-dialog__header) {
  background-color: #f8f6f0 !important;
  padding: 20px !important;
  margin: 0 !important;
  position: relative !important;
}

.ghibli-dialog :deep(.el-dialog__title) {
  color: #5a7247 !important;
  font-family: 'Noto Serif SC', serif !important;
  font-size: 1.3rem !important;
  font-weight: 500 !important;
}

.ghibli-dialog :deep(.el-dialog__body) {
  padding: 30px 20px 20px !important;
}

.ghibli-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #7ca982 !important;
}

.dialog-decoration {
  position: absolute;
  top: 70px;
  left: 0;
  width: 100%;
  height: 10px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 15"><path fill="none" stroke="%23a1c181" stroke-width="2" stroke-dasharray="8,5" d="M0,10 Q250,0 500,10 T1000,10"/></svg>');
  background-repeat: repeat-x;
  background-size: auto 10px;
  opacity: 0.6;
}

/* 表单样式 */
.ghibli-form :deep(.el-form-item__label) {
  color: #5d534a;
  font-family: 'Noto Serif SC', serif;
}

.ghibli-select :deep(.el-input__wrapper) {
  background-color: #f8f6f0 !important;
  box-shadow: 0 0 0 1px #d0cdc7 inset !important;
  border-radius: 8px !important;
}

.ghibli-select :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #a1c181 inset !important;
}

.ghibli-select :deep(.el-input__inner) {
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif;
}

.ghibli-input-number :deep(.el-input__wrapper) {
  background-color: #f8f6f0 !important;
  box-shadow: 0 0 0 1px #d0cdc7 inset !important;
}

.ghibli-input-number :deep(.el-input-number__increase),
.ghibli-input-number :deep(.el-input-number__decrease) {
  background-color: #f3f0e9 !important;
  border-color: #d0cdc7 !important;
  color: #5d534a !important;
}

.ghibli-textarea :deep(.el-textarea__inner) {
  background-color: #f8f6f0 !important;
  border-color: #d0cdc7 !important;
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif !important;
  border-radius: 8px !important;
}

.ghibli-textarea :deep(.el-textarea__inner:focus) {
  border-color: #a1c181 !important;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.ghibli-cancel-btn {
  background-color: #f8f6f0 !important;
  border: 1px solid #d0cdc7 !important;
  color: #5d534a !important;
  border-radius: 20px !important;
  padding: 10px 20px !important;
  font-family: 'Noto Serif SC', serif !important;
  transition: all 0.3s ease !important;
}

.ghibli-cancel-btn:hover {
  border-color: #a1c181 !important;
  color: #5a7247 !important;
}

.ghibli-confirm-btn {
  background-color: #7ca982 !important;
  border: none !important;
  color: white !important;
  border-radius: 20px !important;
  padding: 10px 25px !important;
  font-family: 'Noto Serif SC', serif !important;
  transition: all 0.3s ease !important;
}

.ghibli-confirm-btn:hover {
  background-color: #6a9770 !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1) !important;
}

@media screen and (max-width: 768px) {
  .ghibli-budget {
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
  }
}
</style>