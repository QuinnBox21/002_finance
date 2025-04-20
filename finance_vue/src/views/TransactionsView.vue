<template>
  <div class="ghibli-transactions">
    <div class="page-header">
      <h2>小豆子的账本</h2>
      <el-button class="ghibli-button" @click="handleAdd">
        <i class="el-icon-plus"></i> 新增记录
      </el-button>
    </div>

    <!-- 飘浮的元素 -->
    <div class="floating-elements">
      <div class="floating-element dust-1"></div>
      <div class="floating-element dust-2"></div>
      <div class="floating-element dust-3"></div>
    </div>

    <!-- 搜索过滤 -->
    <div class="ghibli-card filter-card">
      <div class="filter-header">
        <h3>查询筛选</h3>
        <div class="filter-sprite"></div>
      </div>
      
      <el-form :model="filterForm" class="ghibli-filter-form">
        <el-row :gutter="20">
          <el-col :span="6">
        <el-form-item label="类型">
              <el-select v-model="filterForm.type" placeholder="全部类型" class="ghibli-select">
                <el-option label="全部" :value="ALL_TYPE" />
                <el-option label="收入" :value="1" />
            <el-option label="支出" :value="0" />
          </el-select>
        </el-form-item>
          </el-col>

          <el-col :span="6">
        <el-form-item label="类别">
              <el-select v-model="filterForm.category" placeholder="全部类别" class="ghibli-select">
            <el-option
                  v-for="item in categoryOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
            />
          </el-select>
        </el-form-item>
          </el-col>

          <el-col :span="8">
        <el-form-item label="日期范围">
          <el-date-picker
                v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
                class="ghibli-date-picker"
          />
        </el-form-item>
          </el-col>

          <el-col :span="4">
        <el-form-item>
              <div class="filter-buttons">
                <el-button 
                  class="ghibli-search-btn" 
                  @click="handleSearch"
                >
                  查询
                </el-button>
                <el-button 
                  class="ghibli-reset-btn" 
                  @click="resetFilter"
                >
                  重置
                </el-button>
              </div>
        </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="ghibli-card table-card">
      <div class="table-header">
        <h3>交易记录</h3>
        <div class="totoro-icon"></div>
      </div>
      
      <div class="table-wrapper" v-loading="loading">
      <el-table
        :data="tableData"
        border
          class="ghibli-table"
      >
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
              <div class="transaction-type" :class="row.type === 1 ? 'income-type' : 'expense-type'">
              {{ row.type === 1 ? '收入' : '支出' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="category" label="类别" width="120">
            <template #default="{ row }">
              <div class="category-tag">
                {{ row.category }}
              </div>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="金额" width="120">
          <template #default="{ row }">
            <span :class="{ 'income': row.type === 1, 'expense': row.type === 0 }">
              {{ formatAmount(row.amount) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
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

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          :current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="ghibli-pagination"
        />
      </div>
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogType === 'add' ? '新增记录' : '编辑记录'"
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
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="form.type" class="ghibli-radio">
            <el-radio :label="0">支出</el-radio>
            <el-radio :label="1">收入</el-radio>
          </el-radio-group>
        </el-form-item>

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
            :step="0.01"
            :min="0"
            style="width: 100%"
            class="ghibli-input-number"
          />
        </el-form-item>

        <el-form-item label="日期" prop="date">
          <el-date-picker
            v-model="form.date"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
            class="ghibli-date-picker"
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
import { ref, onMounted, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  getTransactions, 
  createTransaction, 
  updateTransaction, 
  deleteTransaction,
  type Transaction
} from '../api/transaction'
import dayjs from 'dayjs'

// 状态数据
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref<'add' | 'edit'>('add')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dateRange = ref<string[]>([])
const formRef = ref<FormInstance>()

// 定义常量
const ALL_TYPE = -1
const ALL_CATEGORY = ''

// 类别列表
const categories = ref<string[]>([
  '餐饮',
  '交通',
  '购物',
  '娱乐',
  '其他'
])

// 筛选表单
const filterForm = ref({
  type: ALL_TYPE as number,
  category: ALL_CATEGORY as string,
  dateRange: [] as string[]
})

const form = ref({
  id: '',
  type: 0 as 0 | 1,
  category: '',
  amount: 0,
  date: dayjs().format('YYYY-MM-DD'),
  remark: ''
})

// 表单验证规则
const rules: FormRules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  category: [{ required: true, message: '请选择类别', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  date: [{ required: true, message: '请选择日期', trigger: 'change' }]
}

// 类别选项
const categoryOptions = ref([
  { label: '全部', value: ALL_CATEGORY },
  { label: '餐饮', value: '餐饮' },
  { label: '交通', value: '交通' },
  { label: '购物', value: '购物' },
  { label: '娱乐', value: '娱乐' },
  { label: '其他', value: '其他' }
])

// 表格数据
 const tableData = ref([])

// 计算是否有激活的筛选条件
const hasActiveFilters = computed(() => {
  return filterForm.value.type !== ALL_TYPE || 
         filterForm.value.category !== ALL_CATEGORY || 
         (dateRange.value && dateRange.value.length === 2)
})

// 获取类别名称
const getCategoryName = (value: string) => {
  const category = categoryOptions.value.find(item => item.value === value)
  return category ? category.label : value
}

// 格式化金额
const formatAmount = (amount: number) => {
  return `¥${amount.toFixed(2)}`
}

// 获取交易记录列表
const fetchTransactions = async () => {
  loading.value = true
  try {
    const { startDate, endDate } = dateRange.value?.length === 2
      ? {
        startDate: dateRange.value[0],
        endDate: dateRange.value[1]
      }
      : { startDate: '', endDate: '' }

    const response = await getTransactions({
      page: currentPage.value,
      size: pageSize.value,
      type: filterForm.value.type,
      category: filterForm.value.category,
      startDate,
      endDate
    })

    tableData.value = response.list || []
    total.value = response.total || 0
  } catch (error) {
    console.error('获取交易记录失败:', error)
    ElMessage.error('获取交易记录失败')
  } finally {
    loading.value = false
  }
}

// 表单提交
const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (dialogType.value === 'add') {
          await createTransaction(form.value)
          ElMessage.success('新增成功')
        } else {
          await updateTransaction(form.value.id, form.value)
          ElMessage.success('更新成功')
        }
        dialogVisible.value = false
  fetchTransactions()
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败')
}
}
  })
}

// 新增记录
const handleAdd = () => {
  dialogType.value = 'add'
  form.value = {
    id: '',
    type: 0,
    category: '',
    amount: 0,
    date: dayjs().format('YYYY-MM-DD'),
    remark: ''
  }
  dialogVisible.value = true
}

// 编辑记录
const handleEdit = (row: any) => {
  dialogType.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

// 删除记录
const handleDelete = (row: any) => {
  ElMessageBox.confirm('确定要删除这条记录吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
    await deleteTransaction(row.id)
      ElMessage.success('删除成功')
    fetchTransactions()
  } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchTransactions()
}

// 重置过滤条件
const resetFilter = () => {
  filterForm.value = {
    type: ALL_TYPE,
    category: ALL_CATEGORY,
    dateRange: []
  }
  currentPage.value = 1
  fetchTransactions()
}

// 处理分页变化
const handleCurrentChange = (page: number) => {
  currentPage.value = page
  fetchTransactions()
}

const handleSizeChange = (size: number) => {
  pageSize.value = size
  currentPage.value = 1
    fetchTransactions()
}

onMounted(() => {
  fetchTransactions()
})
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;500;700&display=swap');

.ghibli-transactions {
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

/* 飘浮的元素 */
.floating-elements {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.floating-element {
  position: absolute;
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.5;
}

.dust-1 {
  top: 10%;
  left: 5%;
  width: 15px;
  height: 15px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><circle cx="12" cy="12" r="8" fill="%235a7247" fill-opacity="0.3"/></svg>');
  animation: float 15s infinite linear;
}

.dust-2 {
  top: 30%;
  right: 10%;
  width: 20px;
  height: 20px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><circle cx="12" cy="12" r="8" fill="%235a7247" fill-opacity="0.3"/></svg>');
  animation: float 20s infinite linear reverse;
}

.dust-3 {
  bottom: 20%;
  left: 20%;
  width: 12px;
  height: 12px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><circle cx="12" cy="12" r="8" fill="%235a7247" fill-opacity="0.3"/></svg>');
  animation: float 18s infinite linear;
}

@keyframes float {
  0% { transform: translate(0, 0); }
  25% { transform: translate(20px, -15px); }
  50% { transform: translate(40px, 0); }
  75% { transform: translate(20px, 15px); }
  100% { transform: translate(0, 0); }
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
  z-index: 1;
}

.ghibli-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.filter-header,
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 2px solid #f3f0e9;
  padding-bottom: 15px;
}

.filter-header h3,
.table-header h3 {
  margin: 0;
  color: #5a7247;
  font-size: 1.2rem;
  font-weight: 500;
}

.filter-sprite {
  width: 32px;
  height: 32px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%235a7247" fill-opacity="0.7" d="M10,13V11H8L14,5V7H16L10,13M19,21H5C3.89,21 3,20.1 3,19V5C3,3.89 3.89,3 5,3H19A2,2 0 0,1 21,5V19A2,2 0 0,1 19,21M19,19V5H5V19H19Z" /></svg>');
  background-size: contain;
  background-repeat: no-repeat;
}

.totoro-icon {
  width: 36px;
  height: 36px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512"><path fill="%235d534a" d="M256,224c-79.41,0-192,122.76-192,200.25,0,34.9,26.81,55.75,71.74,55.75,48.84,0,81.61-25.08,120.26-25.08,38.47,0,72.31,24.34,120.26,24.34,43.44,0,71.74-20.22,71.74-55.75C448,346.16,335.23,224,256,224Z"/><path fill="%235d534a" d="M256,288s-32-32-32-64,32-96,32-96,32,64,32,96S256,288,256,288Z"/><path fill="%235d534a" d="M288,128s32,32,32,64S288,256,288,256s32-32,32-64S288,128,288,128Z"/><path fill="%235d534a" d="M224,128s-32,32-32,64,32,64,32,64-32-32-32-64S224,128,224,128Z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.7;
}

/* 表单样式 */
.ghibli-form :deep(.el-form-item__label) {
  color: #5d534a;
  font-family: 'Noto Serif SC', serif;
}

.ghibli-select :deep(.el-input__wrapper),
.ghibli-date-picker :deep(.el-input__wrapper) {
  background-color: #f8f6f0 !important;
  box-shadow: 0 0 0 1px #d0cdc7 inset !important;
  border-radius: 8px !important;
}

.ghibli-select :deep(.el-input__wrapper:hover),
.ghibli-date-picker :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #a1c181 inset !important;
}

.ghibli-select :deep(.el-input__inner),
.ghibli-date-picker :deep(.el-input__inner) {
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif;
}

/* 查询和重置按钮样式 */
.filter-buttons {
  display: flex;
  gap: 10px;
  height: 100%;
  align-items: center;
}

.ghibli-search-btn {
  background-color: #7ca982 !important;
  border: none !important;
  color: white !important;
  border-radius: 20px !important;
  padding: 8px 20px !important;
  font-family: 'Noto Serif SC', serif !important;
  transition: all 0.3s ease !important;
}

.ghibli-search-btn:hover {
  background-color: #6a9770 !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1) !important;
}

.ghibli-reset-btn {
  background-color: #f8f6f0 !important;
  border: 1px solid #d0cdc7 !important;
  color: #5d534a !important;
  border-radius: 20px !important;
  padding: 8px 20px !important;
  font-family: 'Noto Serif SC', serif !important;
  transition: all 0.3s ease !important;
}

.ghibli-reset-btn:hover {
  border-color: #a1c181 !important;
  color: #5a7247 !important;
  transform: translateY(-2px);
}

/* 表格样式 */
.table-wrapper {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
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

.transaction-type {
  padding: 4px 10px;
  border-radius: 20px;
  text-align: center;
  font-size: 0.9rem;
}

.income-type {
  background-color: rgba(124, 169, 130, 0.2);
  color: #7ca982;
}

.expense-type {
  background-color: rgba(232, 116, 97, 0.2);
  color: #e87461;
}

.category-tag {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 15px;
  background-color: #f3f0e9;
  color: #5d534a;
  font-size: 0.9rem;
}

.income {
  color: #7ca982;
  font-weight: 500;
}

.expense {
  color: #e87461;
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

/* 分页容器 */
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.ghibli-pagination :deep(.btn-prev),
.ghibli-pagination :deep(.btn-next),
.ghibli-pagination :deep(.el-pager li) {
  background-color: #f8f6f0 !important;
  color: #5d534a !important;
  border: none !important;
  transition: all 0.3s ease !important;
}

.ghibli-pagination :deep(.el-pager li.is-active) {
  background-color: #a1c181 !important;
  color: white !important;
}

.ghibli-pagination :deep(.el-pagination__sizes .el-select .el-input) {
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

.ghibli-radio :deep(.el-radio__label) {
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif !important;
}

.ghibli-radio :deep(.el-radio__input.is-checked .el-radio__inner) {
  background-color: #7ca982 !important;
  border-color: #7ca982 !important;
}

.ghibli-radio :deep(.el-radio__input.is-checked + .el-radio__label) {
  color: #7ca982 !important;
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
  .ghibli-transactions {
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
  
  .ghibli-form {
    display: flex;
    flex-direction: column;
  }
  
  .ghibli-form :deep(.el-form-item) {
    margin-right: 0 !important;
    width: 100% !important;
  }
  
  .filter-buttons {
    display: flex;
    width: 100%;
    justify-content: space-between;
  }
  
  .filter-buttons .el-button {
    flex: 1;
  }
}

/* 筛选卡片样式 */
.filter-card {
  margin-bottom: 20px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.filter-card:hover {
  transform: translateY(-5px);
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #d0cdc7;
}

.filter-header h3 {
  margin: 0;
  color: #5a7247;
  font-size: 1.2rem;
  font-weight: 500;
}

.filter-sprite {
  width: 30px;
  height: 30px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%235a7247" d="M12,3C6.5,3 2,7.5 2,13C2,18.5 6.5,23 12,23C17.5,23 22,18.5 22,13C22,7.5 17.5,3 12,3Z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.7;
}

.ghibli-filter-form {
  padding: 0 20px;
}

.ghibli-select :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.7) !important;
  border-radius: 10px !important;
  box-shadow: 0 0 0 1px rgba(160, 174, 152, 0.3) inset !important;
  padding: 0 15px !important;
  transition: all 0.3s ease !important;
}

.ghibli-select :deep(.el-input__wrapper:hover),
.ghibli-select :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #a1c181 inset !important;
  background-color: rgba(255, 255, 255, 0.9) !important;
}

.ghibli-select :deep(.el-input__inner) {
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif !important;
}

.ghibli-select :deep(.el-select__tags) {
  background-color: transparent !important;
}

.ghibli-select :deep(.el-tag) {
  background-color: rgba(161, 193, 129, 0.2) !important;
  border-color: #a1c181 !important;
  color: #5a7247 !important;
}

/* 筛选标签样式 */
.filter-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
  padding: 10px 0;
  border-top: 1px dashed #d0cdc7;
}

.ghibli-tag {
  background-color: rgba(161, 193, 129, 0.2) !important;
  border-color: #a1c181 !important;
  color: #5a7247 !important;
  border-radius: 15px !important;
  padding: 0 15px !important;
  height: 32px !important;
  line-height: 32px !important;
  font-size: 0.9rem !important;
  transition: all 0.3s ease !important;
}

.ghibli-tag:hover {
  background-color: rgba(161, 193, 129, 0.3) !important;
  transform: translateY(-2px);
}

.ghibli-tag :deep(.el-tag__close) {
  color: #5a7247 !important;
  font-size: 16px !important;
  margin-left: 8px !important;
}

.ghibli-tag :deep(.el-tag__close:hover) {
  background-color: rgba(161, 193, 129, 0.4) !important;
  color: #fff !important;
}

/* 表单标签样式 */
:deep(.el-form-item__label) {
  color: #5a7247 !important;
  font-weight: 500 !important;
  font-family: 'Noto Serif SC', serif !important;
}

/* 日期选择器样式 */
.ghibli-date-picker {
  width: 100%;
}

.ghibli-date-picker :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.7) !important;
  border-radius: 10px !important;
  box-shadow: 0 0 0 1px rgba(160, 174, 152, 0.3) inset !important;
  padding: 0 15px !important;
  transition: all 0.3s ease !important;
}

.ghibli-date-picker :deep(.el-input__wrapper:hover),
.ghibli-date-picker :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #a1c181 inset !important;
  background-color: rgba(255, 255, 255, 0.9) !important;
}

.ghibli-date-picker :deep(.el-input__inner) {
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif !important;
}

.ghibli-date-picker :deep(.el-range-separator) {
  color: #a1c181 !important;
}

.ghibli-date-picker :deep(.el-input__prefix-inner) {
  color: #a1c181 !important;
}
</style>