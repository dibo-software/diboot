<script setup lang="ts">
import { UserFilled, ArrowDown, Moon, Sunny } from '@element-plus/icons-vue'
import { isDark, isSmall, toggleTheme } from '@/utils/theme'
import MessageBell from './message-bell/index.vue'
import MenuSearch from './MenuSearch.vue'
import useAuthStore from '@/store/auth'
import Logo from '@/assets/logo.png'
import useAppStore from '@/store/app'

withDefaults(defineProps<{ showLogo?: boolean }>(), { showLogo: true })

const router = useRouter()
const appStore = useAppStore()
const authStore = useAuthStore()

const logout = async () => {
  await authStore.logout()
}

const goPersonal = () => {
  router.push({ name: 'Personal' }).finally()
}
</script>

<template>
  <div style="height: 100%; display: flex; align-items: center">
    <img v-if="showLogo" :src="Logo" alt="Logo" style="height: 39px" />
    <div :style="$slots.dock ? { width: '50vw' } : {}" style="margin-left: 20px">
      <slot name="dock" />
    </div>
    <div style="position: absolute; right: 1.5rem; display: flex; align-items: center">
      <div :class="$slots.topNav ? 'top-nav' : ''">
        <slot name="topNav" />
      </div>
      <menu-search class="item" />
      <el-icon class="item" :size="22" @click="toggleTheme()">
        <component :is="isDark ? Sunny : Moon" />
      </el-icon>
      <!--      <el-dropdown @command="(command: string) => $i18n.locale = command">-->
      <!--        <div class="item">-->
      <!--          <el-icon :size="22">-->
      <!--            <icon name="Local:Language" />-->
      <!--          </el-icon>-->
      <!--        </div>-->
      <!--        <template #dropdown>-->
      <!--          <el-dropdown-menu>-->
      <!--            <el-dropdown-item-->
      <!--              v-for="item in $i18n.availableLocales"-->
      <!--              :key="item"-->
      <!--              :command="item"-->
      <!--              :disabled="$i18n.locale === item"-->
      <!--            >-->
      <!--              {{ $t('language', {}, { locale: item }) }}-->
      <!--            </el-dropdown-item>-->
      <!--          </el-dropdown-menu>-->
      <!--        </template>-->
      <!--      </el-dropdown>-->
      <message-bell class="item" />
      <el-dropdown
        @command="(command: 'small' | 'large' | 'default') => { appStore.globalSize = command; isSmall = command === 'small' }"
      >
        <div class="item">
          <el-icon :size="22">
            <icon name="Local:TextFontSize" />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="large" :disabled="appStore.globalSize === 'large'">大</el-dropdown-item>
            <el-dropdown-item command="default" :disabled="appStore.globalSize === 'default'">中</el-dropdown-item>
            <el-dropdown-item command="small" :disabled="appStore.globalSize === 'small'">小</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
      <el-dropdown>
        <span class="item">
          <el-avatar :size="36" :icon="UserFilled" :src="authStore.avatar" />
          <span style="margin: 0 8px">{{ authStore.realname }}</span>
          <el-icon>
            <arrow-down />
          </el-icon>
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

  &:hover {
    color: var(--el-color-primary);
  }
}
</style>
