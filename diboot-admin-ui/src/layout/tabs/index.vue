<script setup lang="ts">
import { onBeforeRouteUpdate, RouteLocationNormalized } from 'vue-router'
import { Close, Menu, ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { ElScrollbar } from 'element-plus'
import useViewTabsStore from '@/store/viewTabs'

const viewTabsStore = useViewTabsStore()

const allowClose = (item: RouteLocationNormalized) => !item.meta.affixTab && viewTabsStore.tabList.length > 1
const findTabIndex = (item: RouteLocationNormalized) => viewTabsStore.tabList.findIndex(e => e.name === item?.name)

const route = useRoute()
viewTabsStore.addTab(_.cloneDeep(route))
watch(route, value => {
  viewTabsStore.addTab(_.cloneDeep(value))
})

// 首次加载与第一次跳转路由不进入！！！
onBeforeRouteUpdate(to => {
  console.log('to', to.name)
})

// onBeforeRouteUpdate((to, from, next) => {
//   console.log('to', to.name)
//   next()
// })

// 右键菜单
const menu = reactive<{ route?: RouteLocationNormalized; locator?: any }>({})
const contentMenuTarget = ref()
const contentMenu = ref()

const rightClick = (event: MouseEvent, route: RouteLocationNormalized) => {
  contentMenu.value?.handleClose()
  menu.route = route
  menu.locator = {
    position: 'fixed',
    left: `${event.clientX + 1}px`, // X 轴坐标
    top: `${event.clientY - 10}px` // Y 轴坐标
  }
  setTimeout(() => {
    contentMenuTarget.value?.click()
  }, 0)
}

const showButtons = ref(false)
</script>

<template>
  <div ref="tabsShellRef" style="display: flex; align-items: center">
    <el-icon v-show="showButtons" :size="22" class="icon-button" style="border-right: 1px solid #eee" color="#AAA">
      <arrow-left />
    </el-icon>
    <el-scrollbar ref="tabsScrollRef" style="height: 36px; width: calc(100% - 36px)" @scroll="scroll">
      <div ref="tabsRef" class="tabs">
        <el-button
          v-for="item in viewTabsStore.tabList"
          :key="item.fullPath"
          :type="$route.name === item.name ? 'primary' : ''"
          @contextmenu.prevent="event => rightClick(event, item)"
          @click="$router.push(item.fullPath)"
        >
          {{ item.meta.title }}
          <el-icon v-show="allowClose(item)" :size="18" @click="viewTabsStore.closeTab(item, $router)">
            <Close />
          </el-icon>
        </el-button>
      </div>
    </el-scrollbar>
    <el-icon v-show="showButtons" :size="22" class="icon-button" style="border-left: 1px solid #eee" color="#AAA">
      <arrow-right />
    </el-icon>
    <el-dropdown size="small" style="border-left: 1px solid #eeeeee">
      <el-icon :size="22" class="icon-button" style="border-left: 1px solid #eee" color="#AAA">
        <Menu />
      </el-icon>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="viewTabsStore.closeAllTabs($router)">关闭所有页签</el-dropdown-item>
          <el-dropdown-item command="FullScreen" divided>全屏</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
  <!-- tab 菜单 -->
  <el-dropdown ref="contentMenu" trigger="click" size="small" :style="menu.locator" style="position: fixed">
    <div ref="contentMenuTarget" />
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item :disabled="findTabIndex(menu.route) === 0" @click="viewTabsStore.closeLeftTabs(menu.route)">
          关闭左侧页签
        </el-dropdown-item>
        <el-dropdown-item
          :disabled="findTabIndex(menu.route) === viewTabsStore.tabList.length - 1"
          @click="viewTabsStore.closeRightTabs(menu.route)"
        >
          关闭右侧页签
        </el-dropdown-item>
        <el-dropdown-item
          :disabled="viewTabsStore.tabList.length === 1"
          @click="viewTabsStore.closeOtherTabs(menu.route)"
        >
          关闭其他页签
        </el-dropdown-item>
        <el-dropdown-item command="FullScreen" divided>全屏</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped lang="scss">
.tabs {
  display: flex;
  margin: 3px;

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

  .el-button + .el-button {
    margin-left: 5px;
  }
}

.add-fun {
  width: 33px;
  border-left: 2px solid #eeeeee;
}

.icon-button {
  width: 33px;
  height: 36px;
  cursor: pointer;
}

.icon-button:hover {
  color: var(--el-color-primary);
}
</style>
