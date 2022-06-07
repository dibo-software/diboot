// 滚动高度计算
import useAppStore from '@/store/app'
import { getElementHeight } from '@/utils/document'
import type { Ref } from 'vue'

const appStore = useAppStore()

export interface ScrollbarOption {
  // 盒子高度
  boxHeight: unknown
  // 固定盒子的选择器
  fixedBoxSelectors: string[]
  // 额外的高度，（在固定高度的基础上需要再减去一部分高度）
  extraHeight?: number
}

/**
 * 滚动区域计算
 */
export default (option: ScrollbarOption) => {
  const { boxHeight, fixedBoxSelectors, extraHeight } = option
  const fixedHeight = ref(0)
  /**
   * 计算固定高度
   */
  const computedFixedHeight = () => {
    let tempHeight = 0
    const selectors = fixedBoxSelectors ?? []
    selectors.forEach((selector: string) => {
      tempHeight += getElementHeight(selector) ?? 0
    })
    fixedHeight.value = tempHeight
  }
  // 计算滚动容器允许的最大高度
  const height = computed(() => {
    // 滚动容器高度
    return (boxHeight as Ref<number>).value - fixedHeight.value - (extraHeight ?? 0)
  })
  // 需要计算高度的页面切换组件大小，需要重新计算高度
  watch(
    () => appStore.globalSize,
    () => {
      nextTick(() => {
        // 使用定时器，确保高度获取正确
        const timer = setTimeout(() => {
          clearTimeout(timer)
          computedFixedHeight()
        }, 100)
      })
    }
  )
  return {
    height,
    computedFixedHeight
  }
}
