import { Ref } from 'vue'
export interface TabsOption<T> {
  beforeAddTab: (tab: T) => void
}
export default <T>(option: TabsOption<T>) => {
  const { beforeAddTab } = option
  const activeTab = ref('0')

  const tabs = ref<T[]>([]) as Ref<T[]>

  /**
   * 初始化tabs
   * @param tabsValue
   */
  const initTabs = (tabsValue: T[]) => {
    tabs.value = tabsValue
  }
  /**
   * 添加tab
   */
  const addTab = (tab: T) => {
    beforeAddTab && beforeAddTab(tab)
    tabs.value.push(tab)
    activeTab.value = `${tabs.value.length - 1}`
  }
  /**
   * 删除tab
   * @param removeIndex
   */
  const removeTab = (removeIndex: string | number) => {
    const idx = parseInt(removeIndex as string, 10)
    const currentKey = parseInt(activeTab.value, 10)
    tabs.value.splice(idx, 1)
    activeTab.value = currentKey < idx ? activeTab.value : currentKey > 0 ? `${currentKey - 1}` : '0'
  }

  return {
    activeTab,
    tabs,
    initTabs,
    addTab,
    removeTab
  }
}
