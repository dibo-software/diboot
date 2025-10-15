<script setup lang="ts">
import useAuthStore from '@/store/auth'

import { ArrowDown, UserFilled } from '@element-plus/icons-vue'
const authStore = useAuthStore()

const { curPosition, positions } = storeToRefs(authStore)
const menuVisible = ref(false)
const activeIndex = ref('')
const router = useRouter()

const logout = async () => {
  await authStore.logout()
}

const goPersonal = () => {
  router.push({ name: 'Personal' }).finally()
}
const switchPosition = async (idx: number) => {
  const selectedPosition = positions.value.find((value, index) => index === idx)
  if (!selectedPosition) return
  // 如果当前切换的值 等于 当前岗位 不做处理
  if (
      `${selectedPosition.value}${selectedPosition?.ext?.orgId || ''}` ===
      `${curPosition?.value?.value}${curPosition?.value?.ext?.orgId || ''}`
  )
    return
  // 执行岗位切换
  await authStore.switchPosition(selectedPosition)
}
const handleMenuSelect = (index: string, indexPath: string[]) => {
  if (index === 'personal') goPersonal()
  else if (index === 'logout') logout()
  else if (indexPath[0] === 'switch_position') switchPosition(parseInt(index))
}
</script>
<template>
  <div>
    <!-- 触发下拉菜单的元素 -->
    <span class="trigger-element" @click="menuVisible = !menuVisible">
      <el-avatar :size="36" :icon="UserFilled" :src="authStore.avatar" />
      <span style="margin: 0 8px; color: var(--el-text-color-regular); font-size: var(--el-font-size-base)">{{
          authStore.realname
        }}</span>
      <el-icon>
        <arrow-down />
      </el-icon>
    </span>

    <!-- 使用 ElTeleport 或 v-show 控制弹出层 -->
    <div v-show="menuVisible" class="dropdown-menu-popup">
      <el-menu :default-active="activeIndex" class="nested-menu" @select="handleMenuSelect">
        <el-menu-item index="personal">{{ $t('layout.header.personal') }}</el-menu-item>
        <el-sub-menu v-if="curPosition?.label" index="switch_position">
          <template #title>
            <span>{{ $t('layout.header.switchPosition') }}</span>
          </template>
          <el-menu-item v-for="(position, idx) in positions" :key="idx" :index="idx">
            <el-badge
                :is-dot="`${position.value}${position.ext?.orgId}` === `${curPosition.value}${curPosition?.ext?.orgId}`"
                :offset="[10, 20]"
            >
              {{ position?.label }}{{ position?.ext?.orgName ? ` (${position?.ext?.orgName})` : '' }}
            </el-badge>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="logout">{{ $t('layout.header.logout') }}</el-menu-item>
      </el-menu>
    </div>

    <!-- 用于点击外部关闭菜单的遮罩 -->
    <div v-if="menuVisible" class="overlay" @click="menuVisible = false" />
  </div>
</template>

<style scoped>
.trigger-element {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
}
.dropdown-menu-popup {
  position: absolute;
  top: 120%; /* 位于触发元素下方 */
  left: 40%;
  z-index: 2000; /* 确保在其他元素之上 */
  border: 1px solid var(--el-menu-border-color);
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: var(--el-bg-color-page);
  min-width: 160px;
}
.nested-menu {
  border-right: none; /* 移除 ElMenu 默认的右边框 */
}
.overlay {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1999; /* 位于弹出层之下，其他内容之上 */
  background-color: transparent;
}
</style>
