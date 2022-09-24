<script setup lang="ts">
import * as locales from 'element-plus/lib/locale/index'
import { colorPrimary, isSmall } from '@/utils/theme'
import useAppStore from './store/app'
import { useI18n } from 'vue-i18n'
import moment from 'moment'
import 'moment/dist/locale/zh-tw'
import 'moment/dist/locale/zh-cn'

const appStore = useAppStore()

// 初始化自定义主题色
onMounted(() => {
  appStore.colorPrimary && (colorPrimary.value = appStore.colorPrimary)
  isSmall.value = appStore.globalSize === 'small'
})

const i18n = useI18n()

const locale = ref()

watch(
  i18n.locale,
  value => {
    const localeLowerCase = value.toLowerCase()
    locale.value = Object.values(locales).find(e => e.name === localeLowerCase)
    moment.locale(localeLowerCase)
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

<style>
body {
  margin: 0;
  padding: 0 !important;
  overflow: hidden;
}
</style>
