<template>
  <div class="ghibli-register">
    <div class="register-background">
      <div class="forest-trees"></div>
      <div class="clouds">
        <div class="cloud cloud-1"></div>
        <div class="cloud cloud-2"></div>
        <div class="cloud cloud-3"></div>
      </div>
      <div class="forest-spirit"></div>
    </div>
    
    <div class="register-card-container">
      <div class="register-card">
        <div class="card-header">
          <h2>森林居民登记</h2>
          <div class="blue-leaf"></div>
        </div>
        
        <el-form
          ref="formRef"
          :model="registerForm"
          :rules="rules"
          label-width="0"
          class="ghibli-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="用户名"
              prefix-icon="User"
              class="ghibli-input"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              class="ghibli-input"
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              prefix-icon="Lock"
              show-password
              class="ghibli-input"
            />
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="邮箱"
              prefix-icon="Message"
              class="ghibli-input"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              class="register-button"
              :loading="loading"
              @click="handleRegister"
            >
              成为森林的一员
            </el-button>
          </el-form-item>
          
          <div class="form-footer">
            <el-link @click="router.push('/login')" class="login-link">
              已有账号，返回登录
            </el-link>
          </div>
        </el-form>
      </div>
    </div>
    
    <div class="leaf-particles">
      <div class="leaf leaf-1"></div>
      <div class="leaf leaf-2"></div>
      <div class="leaf leaf-3"></div>
      <div class="leaf leaf-4"></div>
      <div class="leaf leaf-5"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock, Message } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  email: ''
})

// 自定义校验：确认密码
const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.value.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.register({
          username: registerForm.value.username,
          password: registerForm.value.password,
          email: registerForm.value.email
        })
        
        if (success) {
          ElMessage.success('注册成功')
          await router.push('/login')
        }
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Serif+SC:wght@400;500;700&display=swap');

.ghibli-register {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  font-family: 'Noto Serif SC', serif;
}

/* 背景元素 */
.register-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background: linear-gradient(to bottom, #d4e6c4 0%, #f8f6f0 100%);
}

.forest-trees {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 30%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1200 200"><path fill="%235a7247" fill-opacity="0.7" d="M0,200 L0,100 Q50,80 100,100 Q150,120 200,100 Q250,80 300,100 Q350,120 400,100 Q450,80 500,100 Q550,120 600,100 Q650,80 700,100 Q750,120 800,100 Q850,80 900,100 Q950,120 1000,100 Q1050,80 1100,100 Q1150,120 1200,100 L1200,200 Z"/></svg>');
  background-size: cover;
  background-repeat: no-repeat;
  opacity: 0.8;
}

.clouds {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.cloud {
  position: absolute;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
}

.cloud-1 {
  top: 10%;
  left: 15%;
  width: 120px;
  height: 60px;
  animation: float 30s infinite linear;
}

.cloud-2 {
  top: 20%;
  right: 15%;
  width: 90px;
  height: 50px;
  animation: float 20s infinite linear reverse;
}

.cloud-3 {
  top: 5%;
  right: 30%;
  width: 70px;
  height: 35px;
  animation: float 25s infinite linear;
}

@keyframes float {
  0% { transform: translateX(0); }
  50% { transform: translateX(30px); }
  100% { transform: translateX(0); }
}

.forest-spirit {
  position: absolute;
  bottom: 30px;
  left: 5%;
  width: 100px;
  height: 150px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 150"><path fill="%23ffffff" fill-opacity="0.7" d="M50,30 C45,30 40,35 40,45 C40,55 45,70 50,70 C55,70 60,55 60,45 C60,35 55,30 50,30 Z M30,70 C25,70 20,75 20,85 C20,95 25,110 30,110 C35,110 40,95 40,85 C40,75 35,70 30,70 Z M70,70 C65,70 60,75 60,85 C60,95 65,110 70,110 C75,110 80,95 80,85 C80,75 75,70 70,70 Z M50,110 C30,110 10,120 10,140 L90,140 C90,120 70,110 50,110 Z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.8;
}

/* 注册卡片 */
.register-card-container {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
}

.register-card {
  width: 400px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.register-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 5px;
  background: linear-gradient(to right, #94b0da, #a1c181);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px dashed #d0cdc7;
  position: relative;
}

.card-header h2 {
  margin: 0;
  color: #5a7247;
  font-size: 1.5rem;
  font-weight: 500;
}

.blue-leaf {
  width: 30px;
  height: 30px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%2394b0da" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.8;
}

/* 表单样式 */
.ghibli-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ghibli-input :deep(.el-input__wrapper) {
  background-color: rgba(255, 255, 255, 0.7) !important;
  border-radius: 10px !important;
  box-shadow: 0 0 0 1px rgba(160, 174, 152, 0.3) inset !important;
  padding: 0 15px !important;
  transition: all 0.3s ease !important;
}

.ghibli-input :deep(.el-input__wrapper:hover),
.ghibli-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #94b0da inset !important;
  background-color: rgba(255, 255, 255, 0.9) !important;
}

.ghibli-input :deep(.el-input__inner) {
  height: 45px !important;
  color: #5d534a !important;
  font-family: 'Noto Serif SC', serif !important;
}

.ghibli-input :deep(.el-input__prefix-inner) {
  color: #5a7247 !important;
}

.register-button {
  width: 100%;
  height: 45px;
  background: linear-gradient(to right, #94b0da, #7a9ac8) !important;
  border: none !important;
  color: white !important;
  font-size: 1rem !important;
  font-weight: 500 !important;
  font-family: 'Noto Serif SC', serif !important;
  border-radius: 10px !important;
  margin-top: 10px;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 10px rgba(148, 176, 218, 0.3) !important;
}

.register-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(148, 176, 218, 0.4) !important;
}

.form-footer {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.login-link {
  color: #5a7247 !important;
  font-size: 0.9rem !important;
  font-family: 'Noto Serif SC', serif !important;
  text-decoration: none !important;
}

.login-link:hover {
  color: #94b0da !important;
  text-decoration: underline !important;
}

/* 飘落的叶子 */
.leaf-particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.leaf {
  position: absolute;
  width: 24px;
  height: 24px;
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.5;
}

.leaf-1 {
  top: 10%;
  left: 20%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23a1c181" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: fallRotate 15s infinite linear;
}

.leaf-2 {
  top: 5%;
  right: 25%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%2394b0da" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: fallRotate 18s infinite linear 2s;
}

.leaf-3 {
  top: 15%;
  right: 10%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%237ca982" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: fallRotate 20s infinite linear 4s;
}

.leaf-4 {
  top: 8%;
  left: 40%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23a1c181" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: fallRotate 17s infinite linear 1s;
}

.leaf-5 {
  top: 12%;
  right: 45%;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%2394b0da" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
  animation: fallRotate 19s infinite linear 3s;
}

@keyframes fallRotate {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 0.5;
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translateY(100vh) rotate(360deg);
    opacity: 0;
  }
}

@media (max-width: 768px) {
  .register-card {
    width: 320px;
    padding: 20px;
  }
  
  .forest-spirit {
    width: 80px;
    height: 120px;
  }
  
  .ghibli-form {
    gap: 15px;
  }
}
</style>