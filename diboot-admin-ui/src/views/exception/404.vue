<script setup lang="ts">
const router = useRouter()
// 返回首页
const goHome = () => {
  router.push({ name: 'Home', replace: true })
}

// 倒数
const countDown = ref(5)
// 计时器
let timer: NodeJS.Timeout

// 重置计时器
function resetTimer() {
  timer = setTimeout(() => {
    clearTimeout(timer)
    if (--countDown.value === 0) goHome()
    else resetTimer()
  }, 1000)
}

resetTimer()
</script>

<template>
  <div class="content">
    <h1>404</h1>
    <h3>页面不存在或无权访问</h3>
    <el-button @click="goHome">返回首页（{{ countDown }}s）</el-button>
  </div>
</template>

<style scoped>
.content {
  text-align: center;
  margin: calc(50vh - 100px) 0;
}
</style>
