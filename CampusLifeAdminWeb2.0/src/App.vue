<script setup>
</script>

<template>
  <!-- 动态背景 -->
  <div class="fixed inset-0 z-[-1] bg-cyber-bg">
    <div class="light-blob bg-indigo-600 w-96 h-96 top-0 left-0"></div>
    <div class="light-blob bg-pink-600 w-96 h-96 bottom-0 right-0"></div>
  </div>
  
  <router-view />
</template>

<style>
.light-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(90px);
  opacity: 0.3;
  animation: blob 15s infinite alternate;
}

@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}
</style>
