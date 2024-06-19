<script setup lang="ts">
import { onBeforeRouteLeave } from 'vue-router'

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

// 离开时销毁计时器
onBeforeRouteLeave(() => clearTimeout(timer))
</script>

<template>
  <div class="content">
    <div class="content-title">404</div>
    <h3>{{ $t('exception.service404') }}</h3>
    <el-button type="success" plain @click="goHome">{{ $t('exception.backHome') }}（{{ countDown }}s）</el-button>
  </div>
</template>

<style scoped lang="scss">
.content {
  text-align: center;
  margin: 20vh 0;

  &-title {
    font-size: 130px;
    font-weight: bold;
    color: #eadb54;
  }
}
</style>
