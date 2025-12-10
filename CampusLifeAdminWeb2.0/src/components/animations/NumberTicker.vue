<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  value: { type: Number, default: 0 },
  prefix: { type: String, default: '' },
  suffix: { type: String, default: '' }
})

const displayValue = ref(0)

const animateTo = (val) => {
  gsap.to(displayValue, {
    value: val,
    duration: 2,
    ease: "power2.out",
    onUpdate: () => { displayValue.value = Math.round(displayValue.value) }
  })
}

onMounted(() => {
  if (props.value > 0) {
    animateTo(props.value)
  }
})

// 监听value变化，重新触发动画
watch(() => props.value, (newVal) => {
  if (newVal > 0) {
    animateTo(newVal)
  }
})

const formatted = computed(() => displayValue.value.toLocaleString())
</script>

<template>
  <span class="tabular-nums">{{ prefix }}{{ formatted }}{{ suffix }}</span>
</template>
