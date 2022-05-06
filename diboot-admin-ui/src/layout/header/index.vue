<script setup lang="ts">
import { UserFilled, ArrowDown } from '@element-plus/icons-vue'
import useAuthStore from '@/store/auth'
import Logo from '@/assets/logo.png'
import useAppStore from '@/store/app'

const authStore = useAuthStore()
const router = useRouter()

const logout = async () => {
  await authStore.logout()
}

const goPersonal = () => {
  router.push({ name: 'Personal' }).finally()
}

const appStore = useAppStore()
</script>

<template>
  <div style="height: 100%; display: flex; align-items: center">
    <img v-if="appStore.layout !== 'default'" :src="Logo" alt="Logo" style="height: 39px" />
    <div :style="$slots.dock ? { width: '50vw' } : {}" style="margin-left: 20px">
      <slot name="dock" />
    </div>
    <div style="position: absolute; right: 1.5rem; display: flex; align-items: center">
      <div :class="$slots.topNav ? 'top-nav' : ''">
        <slot name="topNav" />
      </div>
      <el-dropdown @command="command => (appStore.globalSize = command)">
        <div class="item">
          <el-icon :size="22">
            <icon-show name="LocalSvg:TextFontSize" />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="large" :disabled="appStore.globalSize === 'large'">large</el-dropdown-item>
            <el-dropdown-item command="default" :disabled="appStore.globalSize === 'default'">default</el-dropdown-item>
            <el-dropdown-item command="small" :disabled="appStore.globalSize === 'small'">small</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-dropdown>
        <span class="item">
          <el-avatar :size="36" :icon="UserFilled" :src="authStore.avatar" />
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

<style scoped lang="scss">
.top-nav {
  max-width: 50vw;
  margin-right: 25px;

  :deep(.el-menu) {
    height: 50px;
  }
}

.item {
  height: 36px;
  display: flex;
  align-items: center;
  padding: 0 10px;
  cursor: pointer;
}
</style>
