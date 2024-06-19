<script setup lang="ts">
import { ArrowDown, UserFilled } from '@element-plus/icons-vue'
import { isDark, isSmall } from '@/utils/theme'
import MessageBell from './message-bell/index.vue'
import MenuSearch from './MenuSearch.vue'
import useAuthStore from '@/store/auth'
import Logo from '@/assets/logo.png'
import useAppStore from '@/store/app'
import LightIcon from '@/assets/icon/light.vue'
import DarkIcon from '@/assets/icon/dark.vue'
import i18n from '@/utils/i18n'

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

const openChatAi = () => router.push('/chat-ai')

const enableI18n = import.meta.env.VITE_APP_ENABLE_I18N === 'true'
</script>

<template>
  <div style="height: 100%; display: flex; align-items: center">
    <img v-if="showLogo" :src="Logo" alt="Logo" style="height: 39px" />
    <div v-if="$slots.dock" class="top-menu">
      <slot name="dock" />
    </div>
    <div style="position: absolute; right: 1.5rem; display: flex; align-items: baseline">
      <div v-if="$slots.topNav" class="top-menu">
        <slot name="topNav" />
      </div>
      <menu-search class="item" />
      <div class="item">
        <el-tooltip effect="dark" :content="$t('layout.header.chatAi')" placement="bottom" :show-after="300">
          <el-icon :size="24" style="color: #21ba45">
            <icon name="Local:ChatAi" @click="openChatAi" />
          </el-icon>
        </el-tooltip>
      </div>
      <el-tooltip
        effect="dark"
        :content="isDark ? $t('layout.header.dark') : $t('layout.header.light')"
        placement="bottom"
        :show-after="300"
      >
        <el-switch
          v-model="isDark"
          class="dark-switch item"
          :active-action-icon="DarkIcon"
          :inactive-action-icon="LightIcon"
        />
      </el-tooltip>
      <el-dropdown
        v-if="enableI18n"
        @command="
          (command: string) => {
            i18n.set(command)
            $i18n.locale = command
          }
        "
      >
        <div class="item">
          <el-icon :size="22">
            <icon name="Local:Language" />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item
              v-for="item in $i18n.availableLocales"
              :key="item"
              :command="item"
              :disabled="$i18n.locale === item"
            >
              {{ $t('language', {}, { locale: item }) }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
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
            <el-dropdown-item command="large" :disabled="appStore.globalSize === 'large'">{{
              $t('layout.header.large')
            }}</el-dropdown-item>
            <el-dropdown-item command="default" :disabled="appStore.globalSize === 'default'">{{
              $t('layout.header.default')
            }}</el-dropdown-item>
            <el-dropdown-item command="small" :disabled="appStore.globalSize === 'small'">{{
              $t('layout.header.small')
            }}</el-dropdown-item>
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
            <el-dropdown-item @click="goPersonal">{{ $t('layout.header.personal') }}</el-dropdown-item>
            <el-dropdown-item divided @click="logout()">{{ $t('layout.header.logout') }}</el-dropdown-item>
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
