<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppStore } from '@/stores/app'
import { routes } from '@/router/routes'
import { filterRoutes, getUserPermissions } from '@/utils/permission'
import { ElMessage } from 'element-plus'
import SidebarItem from './components/SidebarItem.vue'
import avatar1 from '@/assets/avatars/1.png'
import avatar2 from '@/assets/avatars/2.png'
import avatar3 from '@/assets/avatars/3.png'
import avatar4 from '@/assets/avatars/4.png'

const avatars = [avatar1, avatar2, avatar3, avatar4]
const adminAvatar = avatars[Math.floor(Math.random() * 4)]

const route = useRoute()
const router = useRouter()
const appStore = useAppStore()

const isCollapsed = computed(() => appStore.isCollapsed)

// 获取用户信息
const userInfo = computed(() => {
  try {
    const info = localStorage.getItem('userInfo')
    return info ? JSON.parse(info) : null
  } catch {
    return null
  }
})

// 获取显示的用户名
const displayName = computed(() => {
  return userInfo.value?.nickname || userInfo.value?.username || '管理员'
})

// 根据用户权限过滤路由
const menuRoutes = computed(() => {
  const permissions = getUserPermissions()
  const filteredRoutes = filterRoutes(routes, permissions)
  return filteredRoutes.filter(r => !r.hidden)
})

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <div class="flex h-screen w-full relative z-10">
    <!-- 侧边栏 -->
    <div 
      class="h-full flex flex-col transition-all duration-300 border-r border-white/5 backdrop-blur-xl bg-slate-900/50"
      :style="{ width: isCollapsed ? '64px' : '260px' }"
    >
      <!-- Logo -->
      <div class="h-20 flex items-center justify-center shrink-0">
        <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-pink-600 flex items-center justify-center text-white font-bold shadow-[0_0_15px_rgba(236,72,153,0.5)]">
          CL
        </div>
        <span v-if="!isCollapsed" class="ml-3 text-xl font-bold text-white tracking-wide">
          Campus<span class="text-indigo-400">Hub</span>
        </span>
      </div>

      <!-- 菜单 -->
      <el-scrollbar class="flex-1 py-4">
        <el-menu
          :default-active="route.path"
          :collapse="isCollapsed"
          background-color="transparent"
          text-color="#94a3b8"
          active-text-color="#fff"
          class="border-none px-2"
          router
        >
          <SidebarItem 
            v-for="r in menuRoutes" 
            :key="r.path" 
            :item="r" 
            :base-path="r.path" 
          />
        </el-menu>
      </el-scrollbar>
    </div>

    <!-- 主内容区 -->
    <div class="flex-1 flex flex-col min-w-0 overflow-hidden relative">
      <!-- 顶部栏 -->
      <div class="h-16 flex items-center justify-between px-8 z-40 border-b border-white/5 backdrop-blur-sm">
        <button 
          @click="appStore.toggleSidebar" 
          class="p-2 text-slate-400 hover:text-white transition-colors"
        >
          <el-icon size="20">
            <component :is="isCollapsed ? 'Expand' : 'Fold'" />
          </el-icon>
        </button>
        
        <div class="flex items-center gap-4">
          <el-dropdown>
            <div class="flex items-center gap-3 cursor-pointer">
              <img :src="adminAvatar" class="w-8 h-8 rounded-full object-cover" />
              <span class="text-slate-300 text-sm">{{ displayName }}</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人设置</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区 -->
      <div class="flex-1 overflow-auto relative scroll-smooth p-0">
        <router-view />
      </div>
    </div>
  </div>
</template>
