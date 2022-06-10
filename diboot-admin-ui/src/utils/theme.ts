import { useCssVar, useDark, useToggle } from '@vueuse/core'

// 主题 (是否是黑暗的)
export const isDark = useDark()
// 切换主题
export const toggleTheme = useToggle(isDark)

// 主题色配置
const pre = '--el-color-primary'
// 主题色
export const colorPrimary = useCssVar(pre)

// 颜色混合(参考 sass @function mix)
const mix = (color1: string, color2: string, weight: number) => {
  weight = Math.max(Math.min(Number(weight), 1), 0)
  const unit = (color: string, index: number) => parseInt(color.substring(index, index + 2), 16)
  const unitColor = (index: number) =>
    `0${Math.round(unit(color1, index) * weight + unit(color2, index) * (1 - weight)).toString(16)}`.slice(-2)
  return `#${unitColor(1)}${unitColor(3)}${unitColor(5)}`
}
// 混入从主题色
const mixColorPrimary = (color: string, light: string, dark: string) => {
  for (let i = 1; i < 10; i += 1) {
    useCssVar(`${pre}-light-${i}`).value = mix(light, color, i * 0.1)
  }
  useCssVar(`${pre}-dark-2`).value = mix(dark, color, 0.2)
}
// 设置从主题色
const subColorPrimary = (color: string, isDark: boolean) => {
  const bgColor = useCssVar('--el-bg-color').value.trim()
  mixColorPrimary(color, bgColor, isDark ? '#ffffff' : '#000000')
}
// 监听主题色变化
watch(colorPrimary, value => subColorPrimary(value, isDark.value))
// 监听主题变化，随着改变主题色
watch(isDark, value => nextTick(() => subColorPrimary(colorPrimary.value.trim(), value)))

// 字体主题
export const isSmall = ref(false)
// 动态字体/icon大小
export const fontSize = useCssVar('--el-font-size-dynamic')
export const iconSize = useCssVar('--el-icon-size-dynamic')
// 动态字体/icon数值
export const fontSizeNumber = computed(() => {
  return parseInt(fontSize.value.slice(0, -2))
})
export const iconSizeNumber = computed(() => {
  return parseInt(iconSize.value.slice(0, -2))
})

watch(isSmall, val => {
  if (val) {
    fontSize.value = '12px'
    iconSize.value = '18px'
  } else {
    fontSize.value = '14px'
    iconSize.value = '20px'
  }
})
