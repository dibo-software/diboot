<script setup lang="ts">
import { onBeforeRouteLeave, RouteLocationNormalized } from 'vue-router'
import { Close, Menu, ArrowLeft, ArrowRight, CloseBold } from '@element-plus/icons-vue'
import { ElScrollbar } from 'element-plus'
import Draggable from 'vuedraggable'
import useViewTabsStore from '@/store/viewTabs'
import useAppStore from '@/store/app'
import { WatchStopHandle } from 'vue'

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
  let deviation = (tabsRef.value?.clientWidth ?? 500) * 0.8
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
        route,
        value => {
          const index = viewTabsStore.addTab(_.cloneDeep(value))
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
  else {
    if (tab.name !== route.name) router.push(tab.fullPath).finally()
    fullScreen.value = true
  }
  refreshButton()
}
// 全屏Tabs
const fullScreenTabsView = () => {
  if (fullScreen.value) fullScreen.value = false
  else fullScreen.value = 'Tabs'
  refreshButton()
}
</script>

<template>
  <div :class="fullScreen === 'Tabs' ? 'fullScreen' : ''">
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
                <el-button
                  :type="$route.name === element.name ? 'primary' : ''"
                  @click="$router.push(element.fullPath)"
                >
                  {{ element.meta.title }}
                  <el-icon v-show="allowClose(element)" :size="18" @click="closeTab(element)">
                    <Close />
                  </el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item :disabled="findTabIndex(element) === 0" @click="closeLeftTabs(element)">
                      关闭左侧页签
                    </el-dropdown-item>
                    <el-dropdown-item
                      :disabled="findTabIndex(element) === viewTabsStore.tabList.length - 1"
                      @click="closeRightTabs(element)"
                    >
                      关闭右侧页签
                    </el-dropdown-item>
                    <el-dropdown-item :disabled="viewTabsStore.tabList.length === 1" @click="closeOtherTabs(element)">
                      关闭其他页签
                    </el-dropdown-item>
                    <el-dropdown-item divided @click="fullScreenTabView(element)">当前页全屏</el-dropdown-item>
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
            <el-dropdown-item @click="closeAllTabs()">关闭所有页签</el-dropdown-item>
            <el-dropdown-item @click="fullScreenTabsView()">{{ fullScreen ? '关闭' : '开启' }}全屏</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div :class="fullScreen === true ? 'fullScreen' : ''">
      <div v-show="fullScreen === true" class="fullScreen-close" title="关闭全屏" @click="fullScreenTabView()">
        <el-icon :size="20"><close-bold /></el-icon>
      </div>
      <slot :full-screen="fullScreen" />
    </div>
  </div>
</template>

<style scoped lang="scss">
.tabs-hull {
  display: flex;
  align-items: center;
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

    .el-dropdown {
      padding-right: 5px;
    }

    .el-button {
      height: 30px;

      .el-icon {
        border-radius: 100%;
        position: relative;
        right: -6px;

        &:hover {
          color: var(--el-color-white);
          background-color: var(--el-color-primary-light-5);
        }
      }
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

.fullScreen {
  position: absolute;
  width: 100%;
  left: 0;
  top: 0;

  .fullScreen-close {
    width: 39px;
    height: 39px;
    border-bottom-left-radius: 100%;
    position: absolute;
    z-index: 1;
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
