<template>
  <el-container class="ghibli-layout">
    <el-header v-if="isAuthenticated" class="ghibli-header">
      <div class="header-content">
        <AppLogo class="app-logo" />
        <el-menu
          mode="horizontal"
          :router="true"
          class="ghibli-menu"
          :default-active="$route.path"
          background-color="transparent"
          text-color="#654b3a"
          active-text-color="#e87a5d"
        >
          <el-menu-item index="/dashboard">🏠 首页</el-menu-item>
          <el-menu-item index="/transactions">📒 账单管理</el-menu-item>
          <el-menu-item index="/budget">💰 预算设置</el-menu-item>
          <el-menu-item index="/reports">📊 财务报表</el-menu-item>
          <el-menu-item index="/advice">💡 理财建议</el-menu-item>
        </el-menu>
        <div class="user-panel">
          <el-dropdown @command="handleCommand">
            <span class="user-name">
              {{ username }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="ghibli-dropdown">
                <el-dropdown-item command="logout">🚪 退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-main class="ghibli-main">
      <router-view />
    </el-main>
  </el-container>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ArrowDown } from '@element-plus/icons-vue'
import AppLogo from '@/components/AppLogo.vue'
import router from "@/router";

const route = useRoute()
const userStore = useUserStore()

const isAuthenticated = computed(() => userStore.isAuthenticated)
const username = computed(() => userStore.username)

const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.logout()
  }
}
</script>

<style lang="scss" scoped>
.ghibli-layout {
  min-height: 100vh;
  background: linear-gradient(150deg, #f5e6ca 0%, #f0d9b5 100%);
}

.ghibli-header {
  height: 80px !important;
  background: rgba(255, 255, 255, 0.9) !important;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 10px 30px;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    max-width: 1200px;
    margin: 0 auto;
  }

  .title {
    color: #654b3a;
    font-family: 'Ma Shan Zheng', cursive;
    font-size: 24px;
    margin: 0;
    text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
  }
}

.ghibli-menu {
  border: none;
  .el-menu-item {
    font-size: 16px;
    margin: 0 10px;
    border-radius: 20px;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(232, 122, 93, 0.1) !important;
      transform: translateY(-2px);
    }

    &.is-active {
      background: rgba(232, 122, 93, 0.2) !important;
    }
  }
}

.user-panel {
  .user-name {
    color: #654b3a;
    font-size: 16px;
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      color: #e87a5d;
    }
  }
}

.ghibli-main {
  padding: 20px;
  width: 100%;
  flex: 1;
  min-height: calc(100vh - 120px);
}

.ghibli-dropdown {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);

  .el-dropdown-menu__item {
    color: #654b3a;
    font-size: 14px;
    transition: all 0.2s ease;

    &:hover {
      background: rgba(232, 122, 93, 0.1);
      color: #e87a5d;
    }
  }
}
</style>