<script setup lang="ts">
import QrcodeVue from 'qrcode.vue'

interface Props {
  // 二维码的值
  value: string
  // 尺寸
  size?: number
  // 颜色
  foreground?: string
  // 二维码的容错能力等级，取值为 'L', 'M', 'Q', 'H' 之一
  level?: 'L' | 'M' | 'Q' | 'H'
}
withDefaults(defineProps<Props>(), {
  size: 100,
  foreground: '#000',
  level: 'H'
})
</script>

<template>
  <qrcode-vue :value="value" :size="size" :foreground="foreground" :level="level" />
</template>

<style scoped></style>
