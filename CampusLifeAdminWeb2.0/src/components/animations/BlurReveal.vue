<script setup>
import { ref, onMounted } from 'vue'
import gsap from 'gsap'

const props = defineProps({
  delay: { type: Number, default: 0 }
})

const elRef = ref(null)

onMounted(() => {
  gsap.fromTo(elRef.value,
    { opacity: 0, y: 20, filter: 'blur(12px)' },
    { opacity: 1, y: 0, filter: 'blur(0px)', duration: 1, delay: props.delay, ease: "power3.out" }
  )
})
</script>

<template>
  <div ref="elRef">
    <slot />
  </div>
</template>
