<script setup lang="ts">
import { UserFilled, ArrowDown } from '@element-plus/icons-vue'
import { isDark, isSmall } from '@/utils/theme'
import MessageBell from './message-bell/index.vue'
import MenuSearch from './MenuSearch.vue'
import useAuthStore from '@/store/auth'
import Logo from '@/assets/logo.png'
import useAppStore from '@/store/app'
import LightIcon from '@/assets/icon/light.vue'
import DarkIcon from '@/assets/icon/dark.vue'

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
    <div v-if="$slots.dock" class="top-menu">
      <slot name="dock" />
    </div>
    <div style="position: absolute; right: 1.5rem; display: flex; align-items: center">
      <div v-if="$slots.topNav" class="top-menu">
        <slot name="topNav" />
      </div>
      <menu-search class="item" />
      <el-switch
        v-model="isDark"
        class="dark-switch"
        :active-action-icon="DarkIcon"
        :inactive-action-icon="LightIcon"
      />
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
        @command="
          (command: 'small' | 'large' | 'default') => {
            appStore.globalSize = command
            isSmall = command === 'small'
          }
        "
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
.top-menu {
  width: calc(100vw - 500px);
  margin-left: 25px;

  :deep(.el-menu) {
    height: 50px;
    width: calc(100vw - 500px);
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

:deep(.el-switch__core) {
  --el-switch-on-color: #2c2c2c;
  --el-switch-off-color: #f2f2f2;
  --el-switch-border-color: none;

  .el-switch__action {
    width: 14px;
    height: 14px;
  }
}

:deep(.dark-icon) {
  border-radius: 50%;
  color: #cfd3dc;
  background-color: #141414;
}

:deep(.light-icon) {
  color: #606266;
}
</style>
