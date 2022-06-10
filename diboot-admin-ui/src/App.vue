<script setup lang="ts">
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import { colorPrimary, isSmall } from '@/utils/theme'
import useAppStore from './store/app'

const appStore = useAppStore()

// 初始化自定义主题色
onMounted(() => {
  appStore.colorPrimary && (colorPrimary.value = appStore.colorPrimary)
  isSmall.value = appStore.globalSize === 'small'
})

const locale = reactive(zhCn)
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
