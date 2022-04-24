<script setup lang="ts">
import useAuthStore from '@/store/auth'
import { useRouter } from 'vue-router'
const authStore = useAuthStore()
const router = useRouter()
const logout = async () => {
  await authStore.logout()
  router.push({ name: 'Login' }).finally()
}
</script>

<template>
  <div style="display: flex; justify-content: space-between">
    <span>.</span>
    <h2>
      <el-button @click="logout">退出登录</el-button>
      {{ authStore.realname }}
    </h2>
  </div>
</template>

<style scoped></style>
