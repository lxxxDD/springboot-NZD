<template>
  <view class="page">
    <view class="header">
      <u-icon name="arrow-left" size="20" @click="goBack"></u-icon>
      <text class="title">设置</text>
    </view>

    <scroll-view scroll-y class="content">
      <view class="menu">
        <view class="item" @click="showTip('账号与安全')">
          <text class="text">账号与安全</text>
          <u-icon name="arrow-right" color="#9ca3af" size="16"></u-icon>
        </view>
        <view class="item" @click="showTip('通知设置')">
          <text class="text">通知设置</text>
          <u-icon name="arrow-right" color="#9ca3af" size="16"></u-icon>
        </view>
        <view class="item" @click="showTip('隐私')">
          <text class="text">隐私</text>
          <u-icon name="arrow-right" color="#9ca3af" size="16"></u-icon>
        </view>
      </view>

      <view class="menu">
        <view class="item" @click="showAbout">
          <text class="text">关于我们</text>
          <u-icon name="arrow-right" color="#9ca3af" size="16"></u-icon>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<script setup>
function goBack() {
  uni.navigateBack();
}

function showTip(name) {
  uni.showToast({
    title: `「${name}」功能开发中`,
    icon: 'none',
    duration: 1500
  })
}

function showAbout() {
  uni.showModal({
    title: '关于 CampusPal',
    content: '版本：1.0.0\n\n校园生活服务平台，为你提供食堂点餐、报修服务、二手交易、校园活动等一站式服务。\n\n© 2024 CampusPal Team',
    showCancel: false,
    confirmText: '好的'
  })
}
</script>

<style scoped lang="scss">
.page { background: #f7f8fa; min-height: 100vh; }
.header {
  display: flex; align-items: center; gap: 12px; padding: 12px 16px; background: #fff;
  padding-top: calc(12px + var(--status-bar-height));
  .title { font-size: 16px; font-weight: 700; }
}
.content { padding: 12px 16px; }
.menu {
  background: #fff; border-radius: 12px; margin-bottom: 12px; overflow: hidden;
  .item {
    padding: 14px 12px; display: flex; align-items: center; justify-content: space-between;
    border-bottom: 1px solid #f1f5f9;
    &:last-child { border-bottom: none; }
    .text { font-weight: 600; }
  }
}
</style>
