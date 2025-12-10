<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  text: { type: String, required: true },
  delay: { type: Number, default: 0.05 }
})

const charRefs = ref([])

onMounted(() => {
  gsap.fromTo(charRefs.value,
    { y: 50, opacity: 0, filter: 'blur(10px)' },
    {
      y: 0, opacity: 1, filter: 'blur(0px)',
      duration: 0.8, stagger: props.delay, ease: "back.out(1.7)"
    }
  )
})
</script>

<template>
  <span class="inline-block overflow-hidden">
    <span
      v-for="(char, i) in text.split('')"
      :key="i"
      :ref="el => charRefs[i] = el"
      class="inline-block whitespace-pre"
      style="will-change: transform, opacity, filter;"
    >{{ char }}</span>
  </span>
</template>
