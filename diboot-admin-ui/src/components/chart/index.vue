<script setup lang="ts">
import * as echarts from 'echarts'
import type { EChartsOption, EChartsType } from 'echarts'
import elementResizeDetectorMaker from 'element-resize-detector'
import { isDark } from '@/utils/theme'

const props = defineProps<{ option: EChartsOption }>()

const chartDom = ref<HTMLElement>()

const erd = elementResizeDetectorMaker()

let chart: EChartsType

onMounted(() => {
  chart = echarts.init(chartDom.value as HTMLElement, isDark.value ? 'dark' : 'white', { locale: 'ZH' })

  erd.listenTo(
    chartDom.value as HTMLElement,
    _.debounce(() => chart.resize(), 50)
  )

  watch(
    () => props.option,
    value => value && chart.setOption(value),
    { immediate: true, deep: true }
  )
})

onUnmounted(() => {
  erd.uninstall(chartDom.value as HTMLElement)
  chart.dispose()
})
</script>

<template>
  <div ref="chartDom" style="height: 100%" />
</template>
