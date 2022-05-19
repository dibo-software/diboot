<script setup lang="ts">
import { DefineComponent } from 'vue'
import { RouteLocationNormalized } from 'vue-router'
import useAppStore from '@/store/app'
import useViewTabsStore from '@/store/viewTabs'

const appStore = useAppStore()
// Tabs 高度
const tabsHeight = computed(() => (appStore.enableTabs ? 36 : 0))

const props = defineProps<{ fullScreen?: boolean | 'Tabs' }>()

// 滚动区域高度 style
const scrollHeightStyle = computed(() => {
  return {
    height: props.fullScreen
      ? appStore.enableTabs && props.fullScreen === true
        ? '100vh'
        : 'calc(100vh - 36px)'
      : appStore.enableTabs
      ? 'calc(100vh - 86px)'
      : 'calc(100vh - 50px)'
  }
})

// 布局已用高度
const layoutUsedHeight = computed(
  //             是否全屏                                      (全屏包含 Tabs )  (未开启全屏 header + tabs)  padding
  () => (props.fullScreen ? (props.fullScreen === true ? 0 : tabsHeight.value) : 50 + tabsHeight.value) + 20
)
// 为需要已占可视高度的组件bind usedVisibleHeight
const bindUsedVisibleHeight = (component: DefineComponent, route: RouteLocationNormalized) => {
  if (Object.keys(component.type?.props ?? {}).includes('usedVisibleHeight'))
    //                                 布局已用高度    +  bounded-padding
    return { usedVisibleHeight: layoutUsedHeight.value + (route.meta.hollow ? 0 : 20) }
  else return {}
}

// footer
const footerRef = ref<HTMLElement>()
const footerHeight = computed(() => footerRef.value?.offsetHeight ?? 0)

const viewTabsStore = useViewTabsStore()
</script>

<template>
  <el-scrollbar :style="scrollHeightStyle">
    <router-view v-slot="{ Component, route }">
      <transition :name="route.meta.transition" mode="out-in">
        <keep-alive :include="viewTabsStore.cachedViews">
          <div :key="route.fullPath" class="content">
            <div :class="route.meta.hollow ? 'hollow' : 'bounded'">
              <component :is="Component" v-bind="bindUsedVisibleHeight(Component, route)" />
            </div>
            <div v-if="appStore.enableFooter && $slots.footer && !route.meta.hideFooter" ref="footerRef" class="footer">
              <slot name="footer" />
            </div>
          </div>
        </keep-alive>
      </transition>
    </router-view>
  </el-scrollbar>
</template>

<style scoped lang="scss">
.content {
  padding: 10px;
  background-color: var(--el-fill-color-light);

  .hollow {
    min-height: calc(100vh - v-bind('layoutUsedHeight + "px"') - v-bind('footerHeight + "px"'));
  }

  .bounded {
    padding: 10px 15px;
    background-color: var(--el-bg-color);
    border-radius: v-bind('footerHeight ? "5px 5px 0 0" : "5px"');
    min-height: calc(100vh - v-bind('layoutUsedHeight + "px"') - 20px - v-bind('footerHeight + "px"'));
  }

  .footer {
    min-height: 50px;
    color: var(--el-color-info);
    display: flex;
    align-items: center;
    justify-content: space-around;
    border-top: 2px dashed var(--el-border-color-lighter);
    border-radius: 0 0 5px 5px;
    background-color: var(--el-bg-color);
  }
}
</style>
