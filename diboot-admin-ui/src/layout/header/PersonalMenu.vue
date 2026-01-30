<script setup lang="ts">
import useAuthStore from '@/store/auth'

import { ArrowDown, UserFilled, User, Setting, SwitchButton } from '@element-plus/icons-vue'
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
        <el-menu-item index="personal" class="menu-item">
          <el-icon><User /></el-icon>
          <span>{{ $t('layout.header.personal') }}</span>
        </el-menu-item>
        <el-sub-menu v-if="curPosition?.label" index="switch_position" class="submenu-position">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>{{ $t('layout.header.switchPosition') }}</span>
          </template>
          <el-menu-item v-for="(position, idx) in positions" :key="idx" :index="idx" class="submenu-item">
            <el-badge
              :is-dot="`${position.value}${position.ext?.orgId}` === `${curPosition.value}${curPosition?.ext?.orgId}`"
              :offset="[10, 20]"
            >
              {{ position?.label }}{{ position?.ext?.orgName ? ` (${position?.ext?.orgName})` : '' }}
            </el-badge>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="logout" class="menu-item-logout">
          <el-icon><SwitchButton /></el-icon>
          <span>{{ $t('layout.header.logout') }}</span>
        </el-menu-item>
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
  top: calc(100% + 8px);
  right: 0;
  z-index: 2000;
  border: 1px solid var(--el-menu-border-color);
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: var(--el-bg-color-page);
  min-width: 200px;
  overflow: hidden;
}

.nested-menu {
  border-right: none;
}

.menu-item:hover {
  color: var(--el-color-primary);
}

.menu-item-logout {
  color: var(--el-color-warning);
}

.submenu-position {
  margin-top: 4px;
}

.submenu-item:hover {
  background-color: var(--el-fill-color-light);
}

.overlay {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 1999;
  background-color: transparent;
}
</style>
