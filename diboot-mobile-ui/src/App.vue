<script setup lang="ts">
import { Locale } from 'vant'
// 引入英文语言包
import enUS from 'vant/es/locale/lang/en-US'
import zhCN from 'vant/es/locale/lang/zh-CN'

import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const i18nMap: Record<string, any> = {
  en: enUS,
  zhCN: zhCN
}

watch(
  i18n.locale,
  value => {
    Locale.use(value, i18nMap[value])
  },
  {
    immediate: true
  }
)
</script>

<template>
  <RouterView />
</template>

<style lang="scss">
.cell-group {
  margin: var(--van-padding-md);
}
.value-font {
  font-size: 16px;
}
</style>
