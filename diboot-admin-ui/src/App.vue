<script setup lang="ts">
import zhCn from 'element-plus/lib/locale/lang/zh-cn'

const size = ref<'default' | 'small' | 'large'>('default')
const locale = reactive(zhCn)
</script>

<template>
  <el-config-provider :locale="locale" :size="size">
    <router-view />
  </el-config-provider>
</template>

<style>
body {
  margin: 0;
  padding: 0;
}
</style>
