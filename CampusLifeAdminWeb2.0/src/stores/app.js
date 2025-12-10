import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const isCollapsed = ref(false)
  const adminInfo = ref(null)

  function toggleSidebar() {
    isCollapsed.value = !isCollapsed.value
  }

  function setAdminInfo(info) {
    adminInfo.value = info
  }

  function logout() {
    adminInfo.value = null
    localStorage.removeItem('token')
  }

  return {
    isCollapsed,
    adminInfo,
    toggleSidebar,
    setAdminInfo,
    logout
  }
})
