<template>
  <view class="filter-row">
    <scroll-view scroll-x class="cats-scroll" :show-scrollbar="false">
      <view class="cats-flex">
        <view
            v-for="(c, index) in cats"
            :key="c"
            class="cat-chip"
            :class="{ active: index === activeCat }"
            @click="$emit('update:activeCat', index)"
        >
          {{ c }}
        </view>
      </view>
    </scroll-view>
    
    <view class="sort-options">
      <view 
        class="sort-btn" 
        :class="{ active: currentSort === 'newest' }"
        @click="$emit('update:currentSort', 'newest')"
      >最新</view>
      <view 
        class="sort-btn" 
        :class="{ active: currentSort === 'priceAsc' }"
        @click="$emit('update:currentSort', 'priceAsc')"
      >价格↑</view>
      <view 
        class="sort-btn" 
        :class="{ active: currentSort === 'priceDesc' }"
        @click="$emit('update:currentSort', 'priceDesc')"
      >价格↓</view>
    </view>
  </view>
</template>

<script setup>
defineProps({
  cats: {
    type: Array,
    required: true
  },
  activeCat: {
    type: Number,
    required: true
  },
  currentSort: {
    type: String,
    required: true
  }
})

defineEmits(['update:activeCat', 'update:currentSort'])
</script>

<style scoped lang="scss">
$primary: #6366F1;
$text-main: #1E293B;
$text-sub: #64748B;

.filter-row {
  /* Ensure it takes full width if needed */
}

/* Categories */
.cats-scroll { width: 100%; white-space: nowrap; margin-bottom: 16px; }
.cats-flex { display: flex; padding-right: 20px; }

.cat-chip {
  display: inline-flex;
  padding: 8px 16px;
  background: #fff;
  border-radius: 100px;
  font-size: 13px;
  font-weight: 600;
  color: $text-sub;
  margin-right: 10px;
  box-shadow: 0 2px 8px rgba(148, 163, 184, 0.08);
  transition: all 0.2s;

  &.active {
    background: $text-main;
    color: #fff;
    box-shadow: 0 4px 12px rgba(30, 41, 59, 0.2);
    transform: translateY(-1px);
  }
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 0 4px 16px 4px;
}

.sort-btn {
  font-size: 14px;
  color: $text-sub;
  transition: all 0.2s;
  position: relative;
  
  &.active {
    color: $text-main;
    font-weight: 800;
    transform: scale(1.05);
    
    &::after {
      content: '';
      position: absolute;
      bottom: -4px;
      left: 50%;
      transform: translateX(-50%);
      width: 12px;
      height: 3px;
      background: $primary;
      border-radius: 2px;
    }
  }
}
</style>
