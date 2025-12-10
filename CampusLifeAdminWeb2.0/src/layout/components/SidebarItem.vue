<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  item: { type: Object, required: true },
  basePath: { type: String, default: '' }
})

const onlyOneChild = ref(null)

const hasOneShowingChild = (children = [], parent) => {
  const showingChildren = children.filter(item => !item.hidden)
  if (showingChildren.length === 1) {
    onlyOneChild.value = showingChildren[0]
    return true
  }
  if (showingChildren.length === 0) {
    onlyOneChild.value = { ...parent, path: '', noShowingChildren: true }
    return true
  }
  return false
}

const resolvePath = (routePath) => {
  if (!routePath) return props.basePath
  if (routePath.startsWith('/')) return routePath
  const base = props.basePath.replace(/\/$/, '')
  // 确保路径以 / 开头
  const fullPath = `${base}/${routePath}`
  return fullPath.startsWith('/') ? fullPath : `/${fullPath}`
}

const showSingleChild = computed(() => {
  return hasOneShowingChild(props.item.children, props.item) && 
         (!onlyOneChild.value.children || onlyOneChild.value.noShowingChildren) && 
         !props.item.alwaysShow
})
</script>

<template>
  <template v-if="!item.hidden">
    <!-- 没有子菜单，直接显示菜单项 -->
    <template v-if="!item.children || item.children.length === 0">
      <el-menu-item :index="resolvePath(item.path)">
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <template #title>{{ item.meta?.title }}</template>
      </el-menu-item>
    </template>

    <!-- 单个子菜单直接显示 -->
    <template v-else-if="showSingleChild">
      <el-menu-item :index="resolvePath(onlyOneChild.path)">
        <el-icon v-if="onlyOneChild.meta?.icon">
          <component :is="onlyOneChild.meta.icon" />
        </el-icon>
        <template #title>{{ onlyOneChild.meta?.title }}</template>
      </el-menu-item>
    </template>

    <!-- 多个子菜单显示为子菜单组 -->
    <el-sub-menu v-else :index="resolvePath(item.path)">
      <template #title>
        <el-icon v-if="item.meta?.icon">
          <component :is="item.meta.icon" />
        </el-icon>
        <span v-if="item.meta">{{ item.meta.title }}</span>
      </template>
      <SidebarItem 
        v-for="child in item.children" 
        :key="child.path" 
        :item="child" 
        :base-path="resolvePath(item.path)" 
      />
    </el-sub-menu>
  </template>
</template>
