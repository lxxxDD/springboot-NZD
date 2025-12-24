<template>
  <view class="page">
    <view class="bg-decoration"></view>

    <u-navbar title="发起报修" :autoBack="true" bgColor="transparent" leftIconColor="#1e293b" titleStyle="color: #1e293b; font-weight: 600;"></u-navbar>

    <scroll-view scroll-y class="content">
      <view class="form-container animate-enter" style="--delay: 0s">

        <view class="section-card">
          <view class="section-header">
            <view class="section-icon loc">
              <text class="material-symbols-outlined" style="font-size: 16px; color: #fff;">location_on</text>
            </view>
            <text class="section-title">位置信息</text>
          </view>

          <view class="form-group">
            <text class="label">地点类型</text>
            <view class="picker-btn" @click="showPlaceType=true">
              <text :class="{ placeholder: !placeTypeLabel }">{{ placeTypeLabel || '请选择区域' }}</text>
              <u-icon name="arrow-down" color="#94A3B8" size="14"></u-icon>
            </view>
          </view>

          <view class="row-2">
            <view class="form-group">
              <text class="label">楼栋</text>
              <view class="picker-btn" @click="showBuilding=true">
                <text>{{ building || '选择' }}</text>
                <u-icon name="arrow-down" color="#94A3B8" size="14"></u-icon>
              </view>
            </view>
            <view class="form-group">
              <text class="label">房间号</text>
              <view class="input-wrapper">
                <u-input
                    v-model="roomNo"
                    placeholder="如 305"
                    border="none"
                    fontSize="14px"
                    color="#1E293B"
                    placeholderStyle="color: #94A3B8"
                    :customStyle="{ height: '100%', width: '100%' }"
                ></u-input>
              </view>
            </view>
          </view>

          <view class="form-group">
            <text class="label">详细定位</text>
            <view class="blueprint-map" @click="onMapClick">
              <view class="grid-lines"></view>
              <view v-if="!pin" class="map-hint">
                <text class="material-symbols-outlined" style="font-size: 28px; color: #94A3B8;">touch_app</text>
                <text>点击此处标记故障点</text>
              </view>
              <view v-else class="marker-pin" :style="{ left: pin.x + 'px', top: pin.y + 'px' }">
                <view class="pin-head"></view>
                <view class="pin-pulse"></view>
              </view>
            </view>
          </view>
        </view>

        <view class="section-card animate-enter" style="--delay: 0.1s">
          <view class="section-header">
            <view class="section-icon repair">
              <text class="material-symbols-outlined" style="font-size: 16px; color: #fff;">build</text>
            </view>
            <text class="section-title">故障详情</text>
          </view>

          <view class="form-group">
            <text class="label">故障类型</text>
            <view class="type-grid">
              <view
                  v-for="it in issueItems"
                  :key="it.key"
                  class="type-item"
                  :class="{ active: form.issue === it.key }"
                  @click="form.issue = it.key"
              >
                <view class="type-icon" :class="it.colorClass">
                  <text class="material-symbols-outlined"
                        :style="{
                          fontSize: '24px',
                          color: form.issue === it.key ? '#fff' : it.iconColor,
                          transition: 'color 0.2s'
                        }">
                    {{ it.matIcon }}
                  </text>
                </view>
                <text class="type-label">{{ it.label }}</text>
              </view>
            </view>
          </view>

          <view class="form-group">
            <text class="label">故障描述</text>
            <textarea
                v-model="form.desc"
                class="textarea-field"
                placeholder="请详细描述故障情况，例如：空调漏水、插座没电..."
                placeholder-class="input-placeholder"
                maxlength="200"
            ></textarea>
            <text class="char-count">{{ form.desc.length }}/200</text>
          </view>
        </view>

        <view class="section-card animate-enter" style="--delay: 0.2s">
          <view class="section-header">
            <view class="section-icon cam">
              <text class="material-symbols-outlined" style="font-size: 16px; color: #fff;">photo_camera</text>
            </view>
            <text class="section-title">现场照片</text>
            <text class="subtitle">（可选，最多3张）</text>
          </view>

          <view class="upload-wrapper">
            <u-upload
                :fileList="fileList"
                @afterRead="afterRead"
                @delete="deletePic"
                name="1"
                multiple
                :maxCount="3"
                width="80"
                height="80"
            ></u-upload>
          </view>
        </view>

        <view style="height: 100px"></view>
      </view>
    </scroll-view>

    <view class="bottom-action-bar animate-slide-up">
      <u-button type="primary" shape="circle" :loading="submitting" :disabled="submitting" customStyle="width: 100%; height: 48px; background: linear-gradient(135deg, #6366F1 0%, #4F46E5 100%); border: none; font-size: 16px; font-weight: 600;" @click="submit">
        <text>立即提交</text>
        <u-icon name="arrow-right" color="#fff" size="14" v-if="!submitting" style="margin-left: 6px"></u-icon>
      </u-button>
    </view>

    <u-picker :show="showPlaceType" :columns="[placeTypes]" @confirm="e=>{ placeType = e.value[0]; showPlaceType = false; }" @cancel="showPlaceType = false"></u-picker>
    <u-picker :show="showBuilding" :columns="[buildings]" @confirm="e=>{ building = e.value[0]; showBuilding = false; }" @cancel="showBuilding = false"></u-picker>
  </view>
</template>

<script setup>
import { createRepair } from '@/api/repairs.js'
import { uploadFile } from '@/api/request.js'
import { reactive, ref, computed } from 'vue'

const form = reactive({ issue: 'Electric', desc: '', photos: [] })
const submitting = ref(false)

const placeTypes = ['宿舍', '教学楼', '图书馆', '食堂', '操场', '其他']
const showPlaceType = ref(false)
const placeType = ref('宿舍')
const placeTypeLabel = computed(() => placeType.value)

const buildings = ['A栋', 'B栋', 'C栋', 'D栋']
const showBuilding = ref(false)
const building = ref('A栋')
const roomNo = ref('')

const pin = ref(null)
function onMapClick(e) {
  pin.value = { x: e.detail.x - e.currentTarget.offsetLeft, y: e.detail.y - e.currentTarget.offsetTop }
}

// 核心修改：改为使用 material icon names
const issueItems = [
  { key: 'Electric', label: '电路', matIcon: 'bolt', colorClass: 'amber', iconColor: '#F59E0B' },
  { key: 'Water', label: '水管', matIcon: 'water_drop', colorClass: 'blue', iconColor: '#3B82F6' },
  { key: 'Wifi', label: '网络', matIcon: 'wifi', colorClass: 'purple', iconColor: '#8B5CF6' },
  { key: 'Furniture', label: '家具', matIcon: 'chair', colorClass: 'slate', iconColor: '#64748B' },
  { key: 'AC', label: '空调', matIcon: 'ac_unit', colorClass: 'cyan', iconColor: '#06B6D4' },
  { key: 'Other', label: '其他', matIcon: 'more_horiz', colorClass: 'gray', iconColor: '#94A3B8' }
]

const fileList = ref([])
const afterRead = async (event) => {
  // 当 multiple 为 true 时，event.file 是一个数组
  const files = [].concat(event.file)
  let fileListLen = fileList.value.length
  
  // 先将所有文件加入列表，状态设为上传中
  files.forEach((file) => {
    fileList.value.push({ ...file, status: 'uploading', message: '上传中' })
  })
  
  // 逐个上传
  for (let i = 0; i < files.length; i++) {
    const file = files[i]
    const index = fileListLen + i
    
    try {
      const res = await uploadFile(file.url)
      console.log('=== 图片上传响应 ===', res)
      
      // 兼容多种返回格式
      let imgUrl = ''
      if (res.data && res.data.url) {
        imgUrl = res.data.url
      } else if (res.url) {
        imgUrl = res.url
      } else if (typeof res.data === 'string' && res.data.startsWith('http')) {
        imgUrl = res.data
      }

      if (imgUrl) {
        fileList.value[index].status = 'success'
        fileList.value[index].message = ''
        fileList.value[index].url = imgUrl
        form.photos.push(imgUrl)
      } else {
        console.error('无法解析图片URL:', res)
        fileList.value[index].status = 'failed'
        fileList.value[index].message = '上传失败'
      }
    } catch (err) {
      console.error('图片上传失败:', err)
      fileList.value[index].status = 'failed'
      fileList.value[index].message = '上传失败'
    }
  }
}
const deletePic = (event) => {
  const removedUrl = fileList.value[event.index]?.url
  fileList.value.splice(event.index, 1)
  // 从photos中移除对应URL
  const photoIndex = form.photos.indexOf(removedUrl)
  if (photoIndex > -1) {
    form.photos.splice(photoIndex, 1)
  }
}

async function submit() {
  const loc = `${placeType.value} ${building.value || ''} ${roomNo.value || ''}`.trim()
  if (!loc) { return uni.showToast({ title: '请填写完整位置', icon: 'none' }) }
  if (!form.desc) { return uni.showToast({ title: '请描述故障情况', icon: 'none' }) }
  
  // 检查是否有图片正在上传
  const uploading = fileList.value.some(f => f.status === 'uploading')
  if (uploading) {
    return uni.showToast({ title: '图片上传中，请稍候', icon: 'none' })
  }
  
  if (submitting.value) return
  submitting.value = true

  uni.showLoading({ title: '提交中...' })

  try {
    // 图片已在afterRead中上传，直接使用form.photos
    const payload = {
      location: loc,
      issueType: form.issue,
      description: form.desc,
      images: form.photos
    }
    console.log('=== 提交报修数据 ===', payload)
    const res = await createRepair(payload)
    console.log('=== 报修创建成功 ===', res)

    uni.hideLoading()
    uni.showToast({ title: '工单已创建', icon: 'success' })
    setTimeout(() => uni.navigateBack(), 800)
  } catch (err) {
    console.error('提交报修失败:', err)
    uni.hideLoading()
    uni.showToast({ title: '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
/* 样式保持不变，但为 material-symbols 预留了字体变体设置（可选） */
.material-symbols-outlined {
  font-family: 'Material Symbols Outlined';
  font-weight: normal;
  font-style: normal;
  font-size: 24px;  /* Preferred icon size */
  display: inline-block;
  line-height: 1;
  text-transform: none;
  letter-spacing: normal;
  word-wrap: normal;
  white-space: nowrap;
  direction: ltr;
}

/* 变量定义 */
$primary: #6366F1;
$bg-page: #F8FAFC;
$text-main: #1E293B;
$text-sub: #64748B;
$card-radius: 20px;
$input-bg: #F1F5F9;

.page {
  background: $bg-page;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.bg-decoration {
  position: absolute;
  top: -120px;
  right: -80px;
  width: 350px;
  height: 350px;
  background: radial-gradient(circle, rgba(99, 102, 241, 0.08) 0%, rgba(248, 250, 252, 0) 70%);
  border-radius: 50%;
  pointer-events: none;
  z-index: 0;
}

.content {
  padding: 16px 20px;
  height: calc(100vh - 44px);
  position: relative;
  z-index: 1;
}

/* 通用 Section Card */
.section-card {
  background: #fff;
  border-radius: $card-radius;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 16px rgba(148, 163, 184, 0.05);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.section-icon {
  width: 24px;
  height: 24px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;

  &.loc { background: #3B82F6; box-shadow: 0 2px 8px rgba(59, 130, 246, 0.3); }
  &.repair { background: #F59E0B; box-shadow: 0 2px 8px rgba(245, 158, 11, 0.3); }
  &.cam { background: #10B981; box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3); }
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: $text-main;
}

.subtitle {
  font-size: 12px;
  color: #94A3B8;
  font-weight: 400;
}

/* 表单控件 */
.form-group {
  margin-bottom: 16px;
  &:last-child { margin-bottom: 0; }
}

.label {
  display: block;
  font-size: 13px;
  color: $text-sub;
  margin-bottom: 8px;
  font-weight: 500;
}

.picker-btn, .input-wrapper, .textarea-wrapper {
  background: $input-bg;
  border: 1px solid transparent;
  border-radius: 12px;
  transition: all 0.2s;
  box-sizing: border-box;

  &:active {
    background: #fff;
    border-color: $primary;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  height: 48px; /* 明确高度，解决塌陷问题 */
  padding: 0 14px;
}


.picker-btn, .input-field, .textarea-field {
  background: $input-bg;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 12px 14px;
  font-size: 14px;
  color: $text-main;
  transition: all 0.2s;

  &:active, &:focus {
    background: #fff;
    border-color: $primary;
    box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
  }
}

.picker-btn {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.placeholder { color: #94A3B8; }
.input-placeholder { color: #94A3B8; }

.row-2 {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  margin-bottom: 16px;

  .form-group { margin-bottom: 0; }
}

/* 蓝图地图 */
.blueprint-map {
  position: relative;
  height: 140px;
  background: #1E293B;
  border-radius: 16px;
  overflow: hidden;
  border: 2px solid #334155;
  box-shadow: inset 0 0 20px rgba(0,0,0,0.5);
}

.grid-lines {
  width: 100%;
  height: 100%;
  background-image:
      linear-gradient(rgba(255,255,255,0.05) 1px, transparent 1px),
      linear-gradient(90deg, rgba(255,255,255,0.05) 1px, transparent 1px);
  background-size: 20px 20px;
}

.map-hint {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;

  text { font-size: 12px; color: #94A3B8; }
}

.marker-pin {
  position: absolute;
  transform: translate(-50%, -50%);
}

.pin-head {
  width: 12px;
  height: 12px;
  background: #EF4444;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.3);
  position: relative;
  z-index: 2;
}

.pin-pulse {
  position: absolute;
  top: 50%; left: 50%;
  transform: translate(-50%, -50%);
  width: 30px;
  height: 30px;
  background: rgba(239, 68, 68, 0.4);
  border-radius: 50%;
  animation: pulse 1.5s infinite;
  z-index: 1;
}

/* 问题类型网格 */
.type-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.type-item {
  background: $input-bg;
  border: 1px solid transparent;
  border-radius: 12px;
  padding: 12px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;

  &.active {
    background: $primary;
    border-color: $primary;
    box-shadow: 0 4px 12px rgba(99, 102, 241, 0.3);
    transform: translateY(-1px);

    .type-label { color: #fff; font-weight: 600; }
    .type-icon { background: rgba(255,255,255,0.2); }
  }
}

.type-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;

  &.amber { background: #FEF3C7; }
  &.blue { background: #EFF6FF; }
  &.purple { background: #F3E8FF; }
  &.slate { background: #E2E8F0; }
  &.cyan { background: #ECFEFF; }
  &.gray { background: #F1F5F9; }
}

.type-label {
  font-size: 11px;
  color: $text-sub;
  transition: color 0.2s;
}

/* 文本域 */
.textarea-field {
  width: 100%;
  height: 100px;
  line-height: 1.4;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 10px;
  color: #CBD5E1;
  margin-top: 4px;
}

/* 底部操作栏 */
.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 12px 20px;
  padding-bottom: calc(12px + env(safe-area-inset-bottom));
  box-shadow: 0 -4px 16px rgba(0,0,0,0.05);
  z-index: 10;
}

.submit-btn {
  background: linear-gradient(135deg, $primary 0%, #4F46E5 100%);
  color: #fff;
  border: none;
  border-radius: 100px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(79, 70, 229, 0.25);

  &:active { transform: scale(0.98); }
}

/* 动画 */
.animate-enter {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeInUp 0.5s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  animation-delay: var(--delay);
}

.animate-slide-up {
  transform: translateY(100%);
  animation: slideUp 0.5s cubic-bezier(0.2, 0.8, 0.2, 1) 0.3s forwards;
}

@keyframes fadeInUp { to { opacity: 1; transform: translateY(0); } }
@keyframes slideUp { to { transform: translateY(0); } }
@keyframes pulse {
  0% { transform: translate(-50%, -50%) scale(0.8); opacity: 0.8; }
  100% { transform: translate(-50%, -50%) scale(2); opacity: 0; }
}
</style>