<script setup lang="ts">
import { UserFilled, ArrowDown } from '@element-plus/icons-vue'
import useAuthStore from '@/store/auth'
import Logo from '@/assets/logo.png'
const authStore = useAuthStore()
const router = useRouter()

const logout = async () => {
  await authStore.logout()
  router.push({ name: 'Login' }).finally()
}

const goPersonal = () => {
  router.push({ name: 'Personal' }).finally()
}
</script>

<template>
  <div style="height: 100%; display: flex; align-items: center">
    <img :src="Logo" alt="Logo" style="height: 39px" />
    <div style="position: absolute; right: 1.5rem">
      <el-dropdown @command="command">
        <span style="display: flex; align-items: center">
          <el-avatar :size="33" :icon="UserFilled" :src="authStore.avatar" />
          <span style="margin: 0 8px">{{ authStore.realname }}</span>
          <el-icon><arrow-down /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="goPersonal">个人中心</el-dropdown-item>
            <el-dropdown-item divided @click="logout()">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<style scoped></style>
