<template>
  <div class="ghibli-login">
    <div class="login-background">
      <div class="forest-trees"></div>
      <div class="clouds">
        <div class="cloud cloud-1"></div>
        <div class="cloud cloud-2"></div>
        <div class="cloud cloud-3"></div>
      </div>
      <div class="totoro"></div>
    </div>
    
    <div class="login-card-container">
      <div class="login-card">
        <div class="card-header">
          <h2>魔法森林入口</h2>
          <div class="green-leaf"></div>
        </div>
        
        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          label-width="0"
          @keyup.enter="handleLogin"
          class="ghibli-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名"
              prefix-icon="User"
              class="ghibli-input"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              show-password
              class="ghibli-input"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              class="login-button"
              :loading="loading"
              @click="handleLogin"
            >
              进入森林
            </el-button>
          </el-form-item>
          
          <div class="form-footer">
            <el-link @click="router.push('/register')" class="register-link">
              初次造访，请先注册
            </el-link>
          </div>
        </el-form>
      </div>
    </div>
    
    <div class="soot-sprites">
      <div class="soot-sprite sprite-1"></div>
      <div class="soot-sprite sprite-2"></div>
      <div class="soot-sprite sprite-3"></div>
      <div class="soot-sprite sprite-4"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const success = await userStore.login(loginForm.value)
        console.log(success)
        if (success) {
          await router.push('/dashboard')
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

.ghibli-login {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  font-family: 'Noto Serif SC', serif;
}

/* 背景元素 */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  background: linear-gradient(to bottom, #c4dfe6 0%, #f8f6f0 100%);
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
  top: 15%;
  left: 10%;
  width: 150px;
  height: 70px;
  animation: float 30s infinite linear;
}

.cloud-2 {
  top: 25%;
  right: 20%;
  width: 100px;
  height: 60px;
  animation: float 20s infinite linear reverse;
}

.cloud-3 {
  top: 10%;
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

.totoro {
  position: absolute;
  bottom: 30px;
  right: 5%;
  width: 150px;
  height: 180px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 120"><ellipse cx="50" cy="90" rx="40" ry="30" fill="%23555555"/><ellipse cx="50" cy="45" rx="30" ry="40" fill="%23555555"/><circle cx="35" cy="40" r="5" fill="white"/><circle cx="65" cy="40" r="5" fill="white"/><circle cx="35" cy="40" r="2" fill="black"/><circle cx="65" cy="40" r="2" fill="black"/><ellipse cx="50" cy="55" rx="10" ry="5" fill="white"/></svg>');
  background-size: contain;
  background-repeat: no-repeat;
  opacity: 0.6;
}

/* 登录卡片 */
.login-card-container {
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2;
}

.login-card {
  width: 380px;
  background-color: rgba(255, 255, 255, 0.9);
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 30px;
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.login-card::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 5px;
  background: linear-gradient(to right, #a1c181, #94b0da);
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

.green-leaf {
  width: 30px;
  height: 30px;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path fill="%23a1c181" d="M17,8C8,10 5.9,16.17 3.82,21.34L5.71,22L6.66,19.7C7.14,19.87 7.64,20 8,20C19,20 22,3 22,3C21,5 14,5.25 9,6.25C4,7.25 2,11.5 2,13.5C2,15.5 3.75,17.25 3.75,17.25C7,8 17,8 17,8Z"/></svg>');
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
  box-shadow: 0 0 0 1px #a1c181 inset !important;
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

.login-button {
  width: 100%;
  height: 45px;
  background: linear-gradient(to right, #a1c181, #7ca982) !important;
  border: none !important;
  color: white !important;
  font-size: 1rem !important;
  font-weight: 500 !important;
  font-family: 'Noto Serif SC', serif !important;
  border-radius: 10px !important;
  margin-top: 10px;
  transition: all 0.3s ease !important;
  box-shadow: 0 4px 10px rgba(161, 193, 129, 0.3) !important;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(161, 193, 129, 0.4) !important;
}

.form-footer {
  display: flex;
  justify-content: center;
  margin-top: 15px;
}

.register-link {
  color: #5a7247 !important;
  font-size: 0.9rem !important;
  font-family: 'Noto Serif SC', serif !important;
  text-decoration: none !important;
}

.register-link:hover {
  color: #a1c181 !important;
  text-decoration: underline !important;
}

/* 煤灰精灵 */
.soot-sprites {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 100px;
  pointer-events: none;
}

.soot-sprite {
  position: absolute;
  width: 20px;
  height: 20px;
  background-size: contain;
  background-repeat: no-repeat;
  background-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><circle cx="10" cy="10" r="8" fill="%23333333"/><circle cx="7" cy="8" r="1.5" fill="%23ffffff"/><circle cx="13" cy="8" r="1.5" fill="%23ffffff"/></svg>');
  opacity: 0.7;
}

.sprite-1 {
  bottom: 20px;
  left: 10%;
  animation: bounce 5s infinite;
}

.sprite-2 {
  bottom: 30px;
  left: 30%;
  animation: bounce 4s infinite 1s;
}

.sprite-3 {
  bottom: 15px;
  right: 30%;
  animation: bounce 6s infinite 0.5s;
}

.sprite-4 {
  bottom: 25px;
  right: 15%;
  animation: bounce 4.5s infinite 2s;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-15px); }
}

@media (max-width: 768px) {
  .login-card {
    width: 300px;
    padding: 20px;
  }
  
  .totoro {
    width: 100px;
    height: 120px;
  }
}
</style>