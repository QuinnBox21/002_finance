<template>
  <div class="app-logo">
    <div class="logo-svg">
      <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64">
        <!-- 背景圆圈 -->
        <circle cx="32" cy="32" r="30" fill="#a1c181" />
        
        <!-- 内部白色圆圈 -->
        <circle cx="32" cy="32" r="26" fill="#f8f6f0" />
        
        <!-- 树和树叶 -->
        <path d="M32,8 C24,16 20,20 20,28 C20,36 25,40 32,40 C39,40 44,36 44,28 C44,20 40,16 32,8 Z" fill="#7ca982" />
        
        <!-- 象征钱币的圆形 -->
        <circle cx="32" cy="32" r="10" fill="#f8f6f0" stroke="#5a7247" stroke-width="2" />
        
        <!-- 字母Q -->
        <path d="M30,27 L30,37 L34,37 L37,40 L37,37 L34,37 L34,27 Z" fill="#5a7247" />
        
        <!-- 小树叶装饰 -->
        <path d="M46,18 C40,19 39,22 38,25 L39,25.5 C40,23 41,23 45,22 C45,22 45,25 47,22 C47,20 46,18 46,18 Z" fill="#94b0da" />
        
        <!-- 小精灵图案 -->
        <circle cx="16" cy="20" r="4" fill="#f8f6f0" opacity="0.7" />
        <circle cx="15" cy="19" r="1" fill="#5d534a" />
        <circle cx="17" cy="19" r="1" fill="#5d534a" />
      </svg>
    </div>
    <div class="logo-text">Quinn原创账本</div>
  </div>
</template>


<script lang="ts">
export default {
  name: 'AppLogo'
}
</script>

<style scoped>
.app-logo {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-svg {
  width: 40px;
  height: 40px;
  animation: gentle-float 3s infinite ease-in-out;
}

.logo-text {
  font-family: 'Ma Shan Zheng', 'Noto Serif SC', serif;
  font-size: 22px;
  font-weight: 500;
  color: #5a7247;
  position: relative;
  transition: all 0.3s ease;
}

.logo-text::after {
  content: "";
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(to right, #a1c181, transparent);
  transition: width 0.3s ease;
}

.app-logo:hover .logo-text::after {
  width: 100%;
}

@keyframes gentle-float {
  0% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
  100% { transform: translateY(0); }
}
</style>
