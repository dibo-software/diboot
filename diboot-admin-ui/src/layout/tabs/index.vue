<script setup lang="ts">
import type { RouteLocationNormalized } from 'vue-router'
import type { WatchStopHandle } from 'vue'
import type { ElScrollbar } from 'element-plus'
import { onBeforeRouteLeave } from 'vue-router'
import { Close, FullScreen, Menu, ArrowLeft, ArrowRight, CloseBold } from '@element-plus/icons-vue'
import Draggable from 'vuedraggable'
import useViewTabsStore from '@/store/view-tabs'
import useAppStore from '@/store/app'

const appStore = useAppStore()

// tabs 滚动控制
const tabsRef = ref()
const tabsScrollRef = ref<InstanceType<typeof ElScrollbar>>()

const showButtons = ref(false) // computed(() => (tabsRef.value?.clientWidth ?? 0) < (tabsRef.value?.scrollWidth ?? 0))
// 刷新按钮
const refreshButton = () =>
  nextTick(() => (showButtons.value = (tabsRef.value?.clientWidth ?? 0) < (tabsRef.value?.scrollWidth ?? 0)))
onMounted(() => refreshButton())

// tabs 位置
const tabsPosition = ref(0)

const scroll = ({ scrollLeft }: { scrollLeft: number }) => {
  tabsPosition.value = scrollLeft
}

// 手动滚动 Tabs 列表
const operateScroll = (right = true) => {
  const deviation = (tabsRef.value?.clientWidth ?? 500) * 0.8
  tabsScrollRef.value?.scrollTo({ left: tabsPosition.value + (right ? deviation : -deviation), behavior: 'smooth' })
}

// tabs 收集
const viewTabsStore = useViewTabsStore()

const allowClose = (item: RouteLocationNormalized) => !item.meta.affixTab && viewTabsStore.tabList.length > 1
const findTabIndex = (item?: RouteLocationNormalized) => viewTabsStore.tabList.findIndex(e => e.name === item?.name)

const route = useRoute()
const router = useRouter()
let collectTabs: WatchStopHandle
watch(
  () => appStore.enableTabs,
  value => {
    if (value) {
      // 收集 Tabs
      collectTabs = watch(
        () => route.fullPath,
        () => {
          const { name, fullPath, meta } = route
          const index = viewTabsStore.addTab({ name, fullPath, meta } as RouteLocationNormalized)
          refreshButton().then(() =>
            tabsRef.value?.childNodes[0].childNodes[index]?.scrollIntoView({ behavior: 'smooth' })
          )
        },
        { immediate: true }
      )
    } else if (collectTabs) {
      collectTabs() // 关闭监听
      viewTabsStore.tabList = []
    }
  },
  { immediate: true }
)
onUnmounted(() =>
  onBeforeRouteLeave(() => {
    appStore.enableTabs && viewTabsStore.tabList.splice(viewTabsStore.tabList.length - 1)
  })
)

// tab 操作
const closeTab = (tab: RouteLocationNormalized) => {
  viewTabsStore.closeTab(tab, router)
  refreshButton()
}
const closeLeftTabs = (tab?: RouteLocationNormalized) => {
  if (tab == null) return
  viewTabsStore.closeLeftTabs(tab, router)
  refreshButton()
}
const closeRightTabs = (tab?: RouteLocationNormalized) => {
  if (tab == null) return
  viewTabsStore.closeRightTabs(tab, router)
  refreshButton()
}
const closeOtherTabs = (tab?: RouteLocationNormalized) => {
  if (tab == null) return
  viewTabsStore.closeOtherTabs(tab, router)
  refreshButton()
}
const closeAllTabs = () => {
  viewTabsStore.closeAllTabs(router)
  refreshButton()
}

// 全屏
const fullScreen = ref<boolean | 'Tabs'>()
// 全屏指定Tab
const fullScreenTabView = (tab?: RouteLocationNormalized) => {
  if (tab == null) fullScreen.value = false
  else if (tab.name === route.name) fullScreen.value = true
  else router.push(tab.fullPath).then(() => (fullScreen.value = true))
  refreshButton()
}
// 全屏含Tabs
const fullScreenTabsView = (open = !fullScreen.value) => {
  fullScreen.value = open ? 'Tabs' : false
  refreshButton()
}

provide('full-screen', (open = true) => fullScreenTabView(open ? route : undefined))
provide('full-screen-tabs', fullScreenTabsView)
provide('update-tab-title', (title: string) => title && viewTabsStore.updateTabTitle(route, title))
</script>

<template>
  <div :class="fullScreen === 'Tabs' ? 'full-screen' : ''">
    <div v-if="appStore.enableTabs" class="tabs-hull">
      <el-icon
        v-show="showButtons"
        :size="22"
        class="icon-button"
        style="border-right: 1px solid var(--el-border-color-lighter)"
        color="#AAA"
        @click="operateScroll(false)"
      >
        <arrow-left />
      </el-icon>
      <el-scrollbar ref="tabsScrollRef" class="tabs-scrollbar" @scroll="scroll">
        <div ref="tabsRef">
          <draggable :list="viewTabsStore.tabList" animation="300" item-key="fullPath" class="tabs">
            <template #item="{ element }">
              <el-dropdown trigger="contextmenu">
                <div
                  class="tab"
                  :class="{ 'is-active': $route.name === element.name }"
                  @click="$router.push(element.fullPath)"
                >
                  {{ $t(element.meta.title) }}
                  <el-icon v-show="allowClose(element)" :size="15" @click.stop="closeTab(element)">
                    <Close />
                  </el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :disabled="findTabIndex(element) === 0" @click="closeLeftTabs(element)">
                      {{ $t('layout.tabs.closeLeft') }}
                    </el-dropdown-item>
                    <el-dropdown-item
                      :disabled="findTabIndex(element) === viewTabsStore.tabList.length - 1"
                      @click="closeRightTabs(element)"
                    >
                      {{ $t('layout.tabs.closeRight') }}
                    </el-dropdown-item>
                    <el-dropdown-item :disabled="viewTabsStore.tabList.length === 1" @click="closeOtherTabs(element)">
                      {{ $t('layout.tabs.closeOther') }}
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="fullScreenTabView(element)">{{
                      $t('layout.tabs.fullScreen')
                    }}</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </draggable>
        </div>
      </el-scrollbar>
      <el-icon
        v-show="showButtons"
        :size="22"
        class="icon-button"
        style="border-left: 1px solid var(--el-border-color-lighter)"
        color="#AAA"
        @click="operateScroll(true)"
      >
        <arrow-right />
      </el-icon>
      <el-dropdown>
        <el-icon
          :size="22"
          class="icon-button"
          style="border-left: 1px solid var(--el-border-color-lighter)"
          color="#AAA"
        >
          <Menu />
        </el-icon>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item :icon="Close" @click="closeAllTabs()">{{ $t('layout.tabs.closeAll') }}</el-dropdown-item>
            <el-dropdown-item :icon="FullScreen" @click="fullScreenTabsView()">
              {{ fullScreen ? $t('layout.tabs.closeFullScreen') : $t('layout.tabs.openFullScreen') }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div :class="fullScreen === true ? 'full-screen' : ''">
      <div
        v-show="fullScreen === true"
        class="full-screen-close"
        :title="$t('layout.tabs.closeFullScreen')"
        @click="fullScreenTabView()"
      >
        <el-icon :size="20">
          <close-bold />
        </el-icon>
      </div>
      <slot :full-screen="fullScreen" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.tabs-hull {
  display: flex;
  align-items: flex-end;
  border-bottom: 1px solid var(--el-border-color-lighter);
  background-color: var(--el-bg-color);

  .tabs-scrollbar {
    width: 100%;

    &:deep(.el-scrollbar__thumb) {
      display: none;
    }
  }

  .tabs {
    display: flex;
    margin: 0 10px;
    font-size: 13px;
    width: max-content;

    --tab-radius: 8px;
    --tab-radius-reverse: calc(var(--tab-radius) * 1.5);
    --tab-text-color: var(--el-text-color-secondary);
    --tab-bg-color: #0000;

    .tab {
      height: 30px;
      display: flex;
      align-items: center;
      padding: 0 8px;
      cursor: pointer;

      color: var(--tab-text-color);
      background-color: var(--tab-bg-color);
      border-top-left-radius: var(--tab-radius);
      border-top-right-radius: var(--tab-radius);

      &:before,
      &:after {
        content: '';
        position: absolute;
        width: var(--tab-radius-reverse);
        height: calc(var(--tab-radius-reverse) * 4 / 5);
        bottom: 0;
      }

      &:before {
        left: calc(0px - var(--tab-radius-reverse));
        background: radial-gradient(circle at 0% 0%, transparent var(--tab-radius-reverse), var(--tab-bg-color) 0);
      }

      &:after {
        right: calc(5px - var(--tab-radius-reverse));
        background: radial-gradient(circle at 100% 0%, transparent var(--tab-radius-reverse), var(--tab-bg-color) 0);
      }

      &:hover {
        --tab-bg-color: var(--el-color-primary-light-8);
      }

      .el-icon {
        border-radius: 100%;
        margin-right: 6px;
        right: -6px;

        &:hover {
          color: var(--el-color-white);
          background-color: var(--el-color-primary-light-5);
        }
      }
    }

    .is-active {
      --tab-text-color: var(--el-color-primary);
      --tab-bg-color: var(--el-color-primary-light-9);
    }

    .el-dropdown {
      padding-right: 5px;
    }
  }

  .icon-button {
    width: 33px;
    height: 35px;
    cursor: pointer;
  }

  .icon-button:hover {
    color: var(--el-color-primary);
  }
}

.full-screen {
  position: absolute;
  width: 100%;
  left: 0;
  top: 0;

  .full-screen-close {
    width: 39px;
    height: 39px;
    border-bottom-left-radius: 100%;
    position: absolute;
    z-index: 999;
    top: 0;
    right: 0;
    cursor: pointer;
    background: rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;

    &:hover {
      background: rgba(0, 0, 0, 0.3);
    }

    .el-icon {
      top: 6px;
      left: 12px;
      position: relative;
      color: var(--el-fill-color-extra-light);
    }
  }
}
</style>
