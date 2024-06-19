<script setup lang="ts">
import * as locales from 'element-plus/es/locale/index'
import { colorPrimary, isSmall } from '@/utils/theme'
import useAppStore from './store/app'
import { useI18n } from 'vue-i18n'
const appStore = useAppStore()
const i18n = useI18n()

const locale = ref()
// 初始化自定义主题色
onMounted(() => {
  appStore.colorPrimary && (colorPrimary.value = appStore.colorPrimary)
  isSmall.value = appStore.globalSize === 'small'
})
watch(
  i18n.locale,
  (value, oldValue) => {
    let localeLowerCase = value.toLowerCase()
    locale.value = Object.values(locales).find(e => e.name === localeLowerCase)
    if (locale.value == null && localeLowerCase.includes('-')) {
      localeLowerCase = localeLowerCase.split('-')[0]
      locale.value = Object.values(locales).find(e => e.name === localeLowerCase)
    }
    if (locale.value == null) {
      locale.value = Object.values(locales).find(e => e.name === i18n.fallbackLocale.value)
    }
    oldValue && oldValue !== value && window.location.reload()
  },
  {
    immediate: true
  }
)
</script>

<template>
  <el-config-provider :locale="locale" :size="appStore.globalSize">
    <router-view />
  </el-config-provider>
</template>

<style lang="scss">
body {
  width: 100% !important;
  margin: 0;
  padding: 0 !important;
  overflow: hidden;
}

.el-color-picker__panel {
  .el-color-dropdown__btn {
    --el-button-text-color: var(--el-color-white);
    --el-button-bg-color: var(--el-color-primary);
    --el-button-border-color: var(--el-color-primary);
    --el-button-outline-color: var(--el-color-primary-light-5);
    --el-button-active-color: var(--el-color-primary-dark-2);
    --el-button-hover-text-color: var(--el-color-white);
    --el-button-hover-link-text-color: var(--el-color-primary-light-5);
    --el-button-hover-bg-color: var(--el-color-primary-light-3);
    --el-button-hover-border-color: var(--el-color-primary-light-3);
    --el-button-active-bg-color: var(--el-color-primary-dark-2);
    --el-button-active-border-color: var(--el-color-primary-dark-2);
    --el-button-disabled-text-color: var(--el-color-white);
    --el-button-disabled-bg-color: var(--el-color-primary-light-5);
    --el-button-disabled-border-color: var(--el-color-primary-light-5);
  }
}
</style>
