<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar title="编辑商品" :autoBack="true" bgColor="transparent" leftIconColor="#1E293B" titleStyle="color: #1E293B; font-weight: 700;" placeholder></u-navbar>

    <scroll-view scroll-y class="content">
      <view class="form-container animate-enter" style="--delay: 0s">

        <view class="section-card">
          <view class="section-header">
            <view class="section-icon img-icon">
              <u-icon name="photo-fill" color="#fff" size="18"></u-icon>
            </view>
            <text class="section-title">商品图片</text>
            <text class="subtitle">{{ form.images.length }}/9</text>
          </view>

          <view class="image-grid">
            <view class="image-item" v-for="(img, index) in form.images" :key="index">
              <image :src="img" mode="aspectFill" class="thumb" @click="previewImage(index)"></image>
              <view class="del-btn" @click.stop="removeImage(index)">
                <u-icon name="close" color="#fff" size="10"></u-icon>
              </view>
              <view class="cover-tag" v-if="index === 0">封面</view>
            </view>

            <view class="add-btn" @click="chooseImage" v-if="form.images.length < 9">
              <u-icon name="plus" color="#CBD5E1" size="24"></u-icon>
              <text class="add-text">添加</text>
            </view>
          </view>
          <text class="tip-text">第一张图片将作为商品封面展示</text>
        </view>

        <view class="section-card animate-enter" style="--delay: 0.1s">
          <view class="section-header">
            <view class="section-icon info">
              <u-icon name="edit-pen-fill" color="#fff" size="18"></u-icon>
            </view>
            <text class="section-title">基本信息</text>
          </view>

          <view class="form-group">
            <text class="label">商品标题</text>
            <view class="input-wrapper">
              <u-input
                  v-model="form.title"
                  placeholder="清晰的标题能吸引更多人"
                  border="none"
                  fontSize="14px"
                  color="#1E293B"
              ></u-input>
            </view>
          </view>

          <view class="form-group">
            <text class="label">价格 (¥)</text>
            <view class="input-wrapper price-input">
              <text class="price-prefix">¥</text>
              <u-input
                  v-model="form.price"
                  type="digit"
                  placeholder="0.00"
                  border="none"
                  fontSize="14px"
                  color="#EF4444"
              ></u-input>
            </view>
            <text class="price-hint">最高 99999.99 元</text>
          </view>

          <view class="row-2">
            <view class="form-group">
              <text class="label">分类</text>
              <picker :range="cats" @change="onCatChange">
                <view class="picker-btn">
                  <text>{{ form.category || '选择分类' }}</text>
                  <u-icon name="arrow-down" color="#94A3B8" size="14"></u-icon>
                </view>
              </picker>
            </view>
            <view class="form-group">
              <text class="label">成色</text>
              <picker :range="conditions" @change="onConditionChange">
                <view class="picker-btn">
                  <text>{{ form.condition || '选择成色' }}</text>
                  <u-icon name="arrow-down" color="#94A3B8" size="14"></u-icon>
                </view>
              </picker>
            </view>
          </view>
        </view>

        <view class="section-card animate-enter" style="--delay: 0.2s">
          <view class="section-header">
            <view class="section-icon desc">
              <u-icon name="file-text-fill" color="#fff" size="18"></u-icon>
            </view>
            <text class="section-title">详细描述</text>
          </view>

          <view class="form-group">
            <view class="textarea-wrapper">
              <u-textarea
                  v-model="form.desc"
                  placeholder="描述物品的新旧程度、入手渠道、转手原因等..."
                  border="none"
                  height="140"
                  count
                  maxlength="500"
                  fontSize="14px"
                  placeholderStyle="color: #94A3B8"
                  :customStyle="{ background: 'transparent', padding: '0' }"
              ></u-textarea>
            </view>
          </view>
        </view>

        <view style="height: 100px"></view>
      </view>
    </scroll-view>

    <view class="bottom-bar animate-slide-up">
      <button class="submit-btn" @click="submit">
        <u-icon name="checkmark" color="#fff" size="18" style="margin-right: 6px"></u-icon>
        <text>保存修改</text>
      </button>
    </view>
  </view>
</template>

<script setup>
import { reactive, ref, watch } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getMarketItemDetail, updateMarketItem, getMarketCategories } from '@/api/market.js'
import { uploadFile } from '@/api/request.js'

const cats = ref([]) // 分类名称列表
const categoryList = ref([]) // 分类完整数据
const conditions = ['全新', '几乎全新', '轻微使用痕迹', '明显使用痕迹']

const loading = ref(true)
const submitting = ref(false)

const form = reactive({
  id: '',
  title: '',
  price: null,
  category: '',
  condition: '',
  images: [],
  desc: ''
})

onLoad(async (options) => {
  // 加载分类列表
  try {
    const res = await getMarketCategories()
    if (res.code === 200 && res.data) {
      categoryList.value = res.data
      cats.value = res.data.map(c => c.name)
    }
  } catch (e) {
    console.error('获取分类失败', e)
  }

  if (options.id) {
    form.id = options.id
    await loadItemDetail(options.id)
  }
})

async function loadItemDetail(id) {
  loading.value = true
  try {
    const res = await getMarketItemDetail(id)
    const item = res.data
    
    form.title = item.title || ''
    form.price = item.price || ''
    form.category = item.category || ''
    form.condition = item.conditionLevel || '几乎全新'
    form.desc = item.description || ''
    
    // 处理图片 - 后端返回的是 JSON 字符串，需要解析
    if (item.images) {
      try {
        // 尝试解析 JSON 字符串
        const parsed = typeof item.images === 'string' 
          ? JSON.parse(item.images) 
          : item.images
        form.images = Array.isArray(parsed) ? parsed : [parsed]
      } catch (e) {
        // 解析失败，可能是逗号分隔的字符串
        form.images = item.images.split(',').filter(Boolean)
      }
    } else if (item.coverImage) {
      form.images = [item.coverImage]
    }
  } catch (err) {
    console.error('加载商品详情失败:', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

function onCatChange(e) {
  form.category = cats.value[e.detail.value]
}

function onConditionChange(e) {
  form.condition = conditions[e.detail.value]
}

// 价格输入限制 - 使用watch监听
watch(() => form.price, (newVal) => {
  if (newVal === null || newVal === '') return
  let val = String(newVal)
  // 移除非数字和小数点
  val = val.replace(/[^\d.]/g, '')
  // 只保留第一个小数点
  const parts = val.split('.')
  if (parts.length > 2) {
    val = parts[0] + '.' + parts.slice(1).join('')
  }
  // 小数点后最多2位
  if (parts.length === 2 && parts[1].length > 2) {
    val = parts[0] + '.' + parts[1].slice(0, 2)
  }
  // 最大值限制
  if (parseFloat(val) > 99999.99) {
    val = '99999.99'
    uni.showToast({ title: '价格不能超过99999.99元', icon: 'none' })
  }
  if (val !== String(newVal)) {
    form.price = val
  }
})

function chooseImage() {
  uni.chooseImage({
    count: 9 - form.images.length,
    success: (res) => {
      form.images.push(...res.tempFilePaths)
    }
  })
}

function removeImage(index) {
  form.images.splice(index, 1)
}

function previewImage(index) {
  uni.previewImage({
    current: index,
    urls: form.images
  })
}

async function submit() {
  if (!form.title || !form.price || !form.images.length || !form.category || !form.condition) {
    return uni.showToast({ title: '请完善必填信息', icon: 'none' })
  }

  if (submitting.value) return
  submitting.value = true

  uni.showLoading({ title: '保存中...' })

  try {
    // 上传新图片（本地临时路径）
    const imageUrls = []
    for (const img of form.images) {
      if (img.startsWith('http')) {
        imageUrls.push(img)
      } else {
        const uploadRes = await uploadFile(img)
        if (uploadRes.data && uploadRes.data.url) {
          imageUrls.push(uploadRes.data.url)
        }
      }
    }

    // 调用更新接口
    await updateMarketItem(form.id, {
      title: form.title,
      price: parseFloat(form.price),
      category: form.category,
      conditionLevel: form.condition,
      images: imageUrls,
      description: form.desc
    })

    uni.hideLoading()
    uni.showToast({ title: '修改成功', icon: 'success' })
    uni.setStorageSync('marketNeedRefresh', true)
    setTimeout(() => uni.navigateBack(), 800)
  } catch (err) {
    console.error('修改失败:', err)
    uni.hideLoading()
    uni.showToast({ title: '修改失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;
$card-radius: 20px;
$input-bg: #F1F5F9;

.page { background: $bg-page; min-height: 100vh; display: flex; flex-direction: column; }

.bg-decoration {
  position: fixed; top: -100px; right: -80px; width: 350px; height: 350px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%; pointer-events: none; z-index: 0;
}

.content { flex: 1; padding: 16px 20px; position: relative; z-index: 1; }

.section-card {
  background: #fff; border-radius: $card-radius; padding: 20px; margin-bottom: 20px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.05);
}

.section-header { display: flex; align-items: center; gap: 8px; margin-bottom: 16px; }
.section-icon {
  width: 28px; height: 28px; border-radius: 8px; display: flex; align-items: center; justify-content: center;
  &.img-icon { background: #F59E0B; }
  &.info { background: #3B82F6; }
  &.desc { background: #10B981; }
}
.section-title { font-size: 15px; font-weight: 700; color: $text-main; }
.subtitle { font-size: 12px; color: $text-sub; margin-left: auto; }

/* 图片网格 */
.image-grid { display: flex; flex-wrap: wrap; gap: 12px; }
.image-item {
  position: relative; width: 80px; height: 80px; border-radius: 12px; overflow: hidden;
  .thumb { width: 100%; height: 100%; }
}
.del-btn {
  position: absolute; top: 4px; right: 4px; width: 18px; height: 18px;
  background: rgba(0,0,0,0.5); border-radius: 50%; display: flex; align-items: center; justify-content: center;
}
.cover-tag {
  position: absolute; bottom: 0; left: 0; right: 0;
  background: rgba(99, 102, 241, 0.8); color: #fff; font-size: 10px; text-align: center; padding: 2px 0;
}
.add-btn {
  width: 80px; height: 80px; background: $input-bg; border-radius: 12px;
  display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 4px;
  border: 1px dashed #CBD5E1;
  &:active { background: #E2E8F0; }
}
.add-text { font-size: 10px; color: #94A3B8; }
.tip-text { font-size: 11px; color: #94A3B8; margin-top: 12px; display: block; }

/* 表单控件 */
.form-group { margin-bottom: 16px; &:last-child { margin-bottom: 0; } }
.label { display: block; font-size: 13px; color: $text-sub; margin-bottom: 8px; font-weight: 500; }

.input-wrapper, .picker-btn, .textarea-wrapper {
  background: $input-bg; border-radius: 12px; transition: all 0.2s;
  &:active { background: #fff; box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.1); }
}
.input-wrapper { height: 48px; display: flex; align-items: center; padding: 0 14px; }
.price-input { 
  .price-prefix { font-size: 16px; font-weight: 700; color: #EF4444; margin-right: 4px; }
}
.price-hint { font-size: 11px; color: #94A3B8; margin-top: 4px; display: block; }
.picker-btn { height: 48px; display: flex; justify-content: space-between; align-items: center; padding: 0 14px; font-size: 14px; color: $text-main; }
.textarea-wrapper { padding: 10px 14px; }

.row-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin-bottom: 16px; .form-group { margin-bottom: 0; } }

/* 底部栏 */
.bottom-bar {
  position: fixed; bottom: 0; left: 0; right: 0;
  background: #fff; padding: 12px 20px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  box-shadow: 0 -4px 16px rgba(0,0,0,0.05); z-index: 10;
}
.submit-btn {
  background: linear-gradient(135deg, $primary 0%, #4F46E5 100%);
  color: #fff; border-radius: 100px; height: 48px;
  display: flex; align-items: center; justify-content: center;
  font-size: 16px; font-weight: 600; border: none;
  box-shadow: 0 8px 20px rgba(79, 70, 229, 0.25);
  &:active { transform: scale(0.98); }
}

.animate-enter { opacity: 0; transform: translateY(20px); animation: fadeUp 0.5s forwards; animation-delay: var(--delay); }
.animate-slide-up { transform: translateY(100%); animation: slideUp 0.5s 0.2s forwards; }
@keyframes fadeUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideUp { to { transform: translateY(0); } }
</style>