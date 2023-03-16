<script setup lang="ts">
import useAppStore from '@/store/app'
import useViewTabsStore from '@/store/view-tabs'
import useAuthStore from '@/store/auth'

const appStore = useAppStore()
// Tabs 高度
const tabsHeight = computed(() => (appStore.enableTabs ? 36 : 0))

const props = defineProps<{ fullScreen?: boolean | 'Tabs' }>()

// 布局已用高度
const layoutUsedHeight = computed(() =>
  //       是否全屏                                      (全屏包含 Tabs )  (未开启全屏 header + tabs)
  props.fullScreen ? (props.fullScreen === true ? 0 : tabsHeight.value) : 50 + tabsHeight.value
)

const viewTabsStore = useViewTabsStore()
const authStore = useAuthStore()
</script>

<template>
  <watermark ref="watermarkRef" :disable="!appStore.enableWatermark" :text="authStore.realname" :rotate="-45">
    <el-scrollbar>
      <div class="content">
        <router-view v-slot="{ Component }">
          <keep-alive :include="viewTabsStore.cachedViews">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </div>
    </el-scrollbar>
  </watermark>
</template>

<style scoped lang="scss">
.content {
  height: calc(100vh - v-bind('layoutUsedHeight + "px"'));
  background-color: var(--el-bg-color);
}
</style>
