<template>
  <view class="page">
    <u-navbar title=" " :autoBack="true" bgColor="#fff" leftIconColor="#1E293B" placeholder border>
      <template #right>
        <view class="nav-right">
          <u-icon name="more-dot-fill" size="24" color="#1E293B"></u-icon>
        </view>
      </template>
    </u-navbar>

    <scroll-view scroll-y class="content">
      <view class="article-container animate-enter">

        <view class="header">
          <text class="title">{{ article.title }}</text>
          <view class="meta-row">
            <view class="meta-left">
              <u-avatar size="24" :src="article.authorAvatar || 'https://via.placeholder.com/100'" shape="circle"></u-avatar>
              <text class="author">{{ article.author || '校园新闻办' }}</text>
              <text class="time">{{ article.time }}</text>
            </view>
            <view class="meta-right">
              <u-icon name="eye" size="14" color="#94A3B8"></u-icon>
              <text class="views">2,341</text>
            </view>
          </view>
        </view>

        <view class="cover-wrap" v-if="article.image">
          <image :src="article.image" mode="widthFix" class="cover-img"></image>
          <text class="img-caption">校园活动现场实拍</text>
        </view>

        <view class="rich-content">
          <u-parse :content="articleContent" :tagStyle="tagStyle"></u-parse>
        </view>

        <view class="article-footer">
          <u-divider text="全 ⽂ 完" textSize="12" textColor="#CBD5E1"></u-divider>

          <view class="tags-box">
            <view class="tag">#校园活动</view>
            <view class="tag">#社团文化</view>
          </view>

          <view class="like-container" @click="toggleLike">
            <view class="like-btn" :class="{ active: isLiked }">
              <u-icon name="thumb-up-fill" size="32" :color="isLiked ? '#fff' : '#64748B'"></u-icon>
            </view>
            <text class="like-text">{{ isLiked ? '已点赞' : '点赞' }}</text>
          </view>
        </view>
      </view>

      <view class="related-section animate-slide-up">
        <view class="sec-title">
          <view class="indicator"></view>
          <text>相关阅读</text>
        </view>

        <view class="related-list">
          <view v-for="item in relatedNews" :key="item.id" class="related-item" @click="openNews(item)">
            <view class="related-info">
              <text class="related-title u-line-2">{{ item.title }}</text>
              <text class="related-meta">{{ item.time }}</text>
            </view>
            <u-image :src="item.image" width="96px" height="72px" radius="8" mode="aspectFill"></u-image>
          </view>
        </view>
      </view>

      <view style="height: 100px"></view>
    </scroll-view>

    <view class="bottom-bar">
      <view class="input-fake">
        <u-icon name="edit-pen" size="16" color="#94A3B8"></u-icon>
        <text>写评论...</text>
      </view>

      <view class="actions">
        <view class="icon-btn" @click="scrollToComments">
          <u-icon name="chat" size="24" color="#64748B"></u-icon>
          <u-badge :value="24" absolute :offset="[-5, -5]" type="error" max="99"></u-badge>
        </view>
        <view class="icon-btn" @click="toggleLike">
          <u-icon :name="isLiked ? 'thumb-up-fill' : 'thumb-up'" size="24" :color="isLiked ? '#6366F1' : '#64748B'"></u-icon>
        </view>
        <view class="icon-btn">
          <u-icon name="share-square" size="24" color="#64748B"></u-icon>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getNewsDetail, getNewsList } from '@/api/news.js'
// mock 数据已移除，使用 API 获取

const article = ref({})
const relatedList = ref([])
const isLiked = ref(false)
const loading = ref(true)

// 文章内容（从API获取或使用默认）
const articleContent = computed(() => {
  return article.value.content || `<p>${article.value.summary || '暂无内容'}</p>`
})

// 固定样式配置 (精心调优的阅读参数)
const tagStyle = {
  p: 'font-size: 16px; line-height: 1.8; color: #334155; margin-bottom: 16px; text-align: justify;',
  strong: 'font-weight: 700; color: #1E293B;',
  h3: 'font-size: 18px; font-weight: 700; color: #1E293B; margin: 24px 0 12px;',
  ul: 'margin-bottom: 16px; padding-left: 20px;',
  li: 'font-size: 16px; color: #475569; margin-bottom: 8px; line-height: 1.6;',
  blockquote: 'font-size: 15px; background: #F8FAFC; border-left: 4px solid #6366F1; padding: 16px; color: #64748B; font-style: italic; border-radius: 8px; margin: 16px 0;'
}

const relatedNews = computed(() => {
  return relatedList.value.filter(n => n.id != article.value.id).slice(0, 3)
})

// 加载新闻详情
async function loadNewsDetail(id) {
  loading.value = true
  try {
    const res = await getNewsDetail(id)
    if (res.code === 200 && res.data) {
      article.value = {
        id: res.data.id,
        title: res.data.title,
        summary: res.data.summary,
        content: res.data.content,
        image: res.data.coverImage,
        author: res.data.author,
        time: formatTime(res.data.publishTime || res.data.createTime),
        viewCount: res.data.viewCount,
        category: res.data.categoryName
      }
    } else {
      // API失败，使用模拟数据
      const found = mockNews.find(n => n.id == id)
      article.value = found || mockNews[0]
    }
  } catch (e) {
    console.log('获取新闻详情失败，使用模拟数据', e)
    const found = mockNews.find(n => n.id == id)
    article.value = found || mockNews[0]
  } finally {
    loading.value = false
  }
}

// 加载相关新闻
async function loadRelatedNews() {
  try {
    const res = await getNewsList({ page: 1, pageSize: 4 })
    if (res.code === 200 && res.data) {
      const list = res.data.list || res.data
      relatedList.value = list.map(item => ({
        id: item.id,
        title: item.title,
        image: item.coverImage,
        time: formatTime(item.publishTime || item.createTime)
      }))
    } else {
      relatedList.value = mockNews
    }
  } catch (e) {
    relatedList.value = mockNews
  }
}

function formatTime(timeStr) {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return `${date.getMonth() + 1}月${date.getDate()}日`
}

onLoad((options) => {
  const id = options.id
  loadNewsDetail(id)
  loadRelatedNews()
})

function toggleLike() {
  isLiked.value = !isLiked.value
  uni.showToast({ title: isLiked.value ? '点赞成功' : '取消点赞', icon: 'none' })
}

function openNews(item) {
  uni.redirectTo({ url: '/pages/news/detail?id=' + item.id })
}

function scrollToComments() {
  uni.showToast({ title: '跳转评论区', icon: 'none' })
}
</script>

<style scoped lang="scss">
/* 变量 */
$bg-page: #fff;
$text-main: #1E293B;
$text-sub: #64748B;
$primary: #6366F1;

.page { background: $bg-page; min-height: 100vh; }
.content { padding: 0 20px; }

/* 头部区 */
.article-container { padding-top: 10px; }
.header { margin-bottom: 24px; }

.title {
  font-size: 22px; font-weight: 800; color: $text-main;
  line-height: 1.4; margin-bottom: 16px; display: block;
}

.meta-row {
  display: flex; justify-content: space-between; align-items: center;
}

.meta-left {
  display: flex; align-items: center; gap: 8px;
  .author { font-size: 13px; font-weight: 600; color: $text-main; }
  .time { font-size: 12px; color: #94A3B8; }
}

.meta-right {
  display: flex; align-items: center; gap: 4px;
  .views { font-size: 12px; color: #94A3B8; }
}

/* 封面图 */
.cover-wrap {
  margin-bottom: 28px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(148, 163, 184, 0.15);

  .cover-img { width: 100%; display: block; }
  .img-caption {
    display: block; text-align: center; font-size: 12px; color: #94A3B8;
    padding: 8px 0; background: #F8FAFC;
  }
}

/* 正文区 */
.rich-content {
  margin-bottom: 40px;
  min-height: 300px;
}

/* 底部互动区 */
.article-footer {
  margin-bottom: 48px;
  display: flex; flex-direction: column; align-items: center;
}

.tags-box {
  display: flex; gap: 10px; margin: 16px 0 32px;
  .tag {
    font-size: 13px; color: $primary; background: #EFF6FF;
    padding: 6px 12px; border-radius: 100px;
  }
}

.like-container {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
}

.like-btn {
  width: 64px; height: 64px; border-radius: 50%;
  background: #F1F5F9;
  display: flex; align-items: center; justify-content: center;
  transition: all 0.2s cubic-bezier(0.175, 0.885, 0.32, 1.275);

  &.active {
    background: $primary;
    box-shadow: 0 8px 20px rgba(99, 102, 241, 0.4);
    transform: scale(1.1);
  }

  &:active { transform: scale(0.95); }
}

.like-text { font-size: 12px; color: $text-sub; }

/* 相关推荐 */
.related-section {
  border-top: 1px solid #F1F5F9;
  margin: 0 -20px;
  padding: 32px 20px;
  background: #FAFAFA;
}

.sec-title {
  display: flex; align-items: center; gap: 8px; margin-bottom: 20px;
  font-size: 17px; font-weight: 700; color: $text-main;

  .indicator { width: 4px; height: 18px; background: $primary; border-radius: 2px; }
}

.related-list { display: flex; flex-direction: column; gap: 16px; }

.related-item {
  display: flex; gap: 16px;
  background: #fff; padding: 12px; border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
}

.related-info {
  flex: 1; display: flex; flex-direction: column; justify-content: space-between; padding: 2px 0;
}

.related-title {
  font-size: 15px; font-weight: 600; color: $text-main; line-height: 1.4;
}

.related-meta { font-size: 12px; color: #94A3B8; }

/* 底部悬浮栏 */
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(10px);
  border-top: 1px solid #F1F5F9;
  padding: 12px 24px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  display: flex; align-items: center; gap: 20px;
  z-index: 100;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.05);
}

.input-fake {
  flex: 1;
  background: #F1F5F9;
  height: 40px;
  border-radius: 20px;
  display: flex; align-items: center; padding: 0 16px; gap: 8px;
  font-size: 14px; color: #94A3B8;
}

.actions { display: flex; align-items: center; gap: 24px; }
.icon-btn { position: relative; display: flex; align-items: center; justify-content: center; }

/* 动画 */
.animate-enter { opacity: 0; transform: translateY(10px); animation: fadeUp 0.5s forwards; }
.animate-slide-up { opacity: 0; transform: translateY(20px); animation: fadeUp 0.5s 0.2s forwards; }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
</style>