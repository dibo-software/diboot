<script setup lang="ts">
import zhCn from 'element-plus/lib/locale/lang/zh-cn'
import useAppStore from './store/app'

const appStore = useAppStore()

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
  padding: 0;
}
</style>
