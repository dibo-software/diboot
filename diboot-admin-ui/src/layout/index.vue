<script setup lang="ts">
import AppHeader from './header/index.vue'
import AppMenu from './memu/index.vue'
import AppMain from './main/index.vue'
import AppTabs from './tabs/index.vue'

import type { Directive } from 'vue'
import type { RouteRecordRaw } from 'vue-router'
import { Menu } from '@element-plus/icons-vue'
import { iconSizeNumber } from '@/utils/theme'

import Logo from '@/assets/logo.png'

import useAppStore from '@/store/app'
import { getMenuTree } from '@/utils/route'

const appStore = useAppStore()

const menuTree = getMenuTree()

const router = useRouter()
const oneLevel = ref<RouteRecordRaw>()
const openOneLevel = (menu: RouteRecordRaw) => {
  const oldOneLevel = oneLevel.value
  oneLevel.value = menu
  if (router.currentRoute.value.name !== menu.name)
    router.push(menu.path).then(navigationFailure => {
      if (navigationFailure) oneLevel.value = oldOneLevel
    })
}

watch(
  () => router.currentRoute.value.matched.find(item => menuTree.includes(item)),
  value => {
    if (value) oneLevel.value = value
  },
  { immediate: true }
)

const isMenuCollapse = ref(false)

// 动态引入配置组件（生产环境舍弃）
const AppSetting = defineAsyncComponent({
  loader: () => (import.meta.env.DEV ? import('./setting/index.vue') : Promise.reject()),
  onError: () => null
})

// 移动端布局
const isMobile = ref(document.body.clientWidth < 992)
window.addEventListener('resize', () => (isMobile.value = document.body.clientWidth < 992))

const showMenu = ref(false)
watch(
  () => router.currentRoute.value.fullPath,
  () => (showMenu.value = false)
)

// 拖拽指令
const vDrag: Directive<HTMLElement> = {
  created(el) {
    el.onmousedown = event => {
      document.onselectstart = () => false
      let isClick = true
      //鼠标按下，计算当前元素距离可视区的距离
      const elX = event.clientX - el.offsetLeft
      const elY = event.clientY - el.offsetTop
      document.onmousemove = e => {
        //通过事件委托，计算移动的距离
        const x = e.clientX - elX
        const y = e.clientY - elY
        //移动当前元素
        if (x > 0 && x < document.body.clientWidth - 50) {
          isClick = false
          el.style.left = x + 'px'
        }
        if (y > 0 && y < document.body.clientHeight - 50) {
          isClick = false
          el.style.top = y + 'px'
        }
      }
      document.onmouseup = () => {
        if (isClick) showMenu.value = true
        document.onselectstart = null
        document.onmousemove = null
        document.onmouseup = null
      }
    }
  }
}
</script>

<template>
  <el-container v-if="isMobile" style="height: 100vh">
    <el-header height="50px" style="border-bottom: 1px solid var(--el-border-color-lighter)">
      <app-header />
    </el-header>
    <app-tabs>
      <template #default="{ fullScreen }">
        <app-main :full-screen="fullScreen" />
      </template>
    </app-tabs>

    <div v-drag class="mobile-menu-button">
      <el-icon :size="30" color="var(--el-color-white)">
        <Menu />
      </el-icon>
      <el-drawer v-model="showMenu" direction="ltr" :with-header="false">
        <app-menu :menu-tree="menuTree" height="100vh" />
      </el-drawer>
    </div>
  </el-container>

  <el-container v-else-if="appStore.layout === 'default'">
    <el-container>
      <el-aside :width="oneLevel?.children?.length ? (isMenuCollapse ? '135px' : '260px') : '71px'">
        <div class="subfield">
          <div class="one-level">
            <div class="one-level-logo">
              <img :src="Logo" alt="Logo" style="height: 39px" />
            </div>
            <el-menu class="default-menu" :default-active="oneLevel?.path">
              <el-menu-item
                v-for="item in menuTree"
                :key="item.name"
                class="default-menu-item"
                :index="item.path"
                :title="item.meta?.title"
                @click="openOneLevel(item)"
              >
                <el-icon v-if="item.meta?.icon" :size="iconSizeNumber">
                  <icon :name="item.meta?.icon" />
                </el-icon>
                <span class="title">{{ item.meta?.title }}</span>
              </el-menu-item>
            </el-menu>
          </div>
          <div v-show="oneLevel?.children?.length" class="submenu">
            <app-menu v-model:collapse="isMenuCollapse" :menu-tree="oneLevel?.children">
              <template #title>
                <strong class="title">{{ oneLevel?.meta?.title }}</strong>
              </template>
            </app-menu>
          </div>
        </div>
      </el-aside>
      <el-container>
        <el-main style="padding: 0">
          <el-header height="50px" style="border-bottom: 1px solid var(--el-border-color-lighter)">
            <app-header :show-logo="false" />
          </el-header>
          <app-tabs>
            <template #default="{ fullScreen }">
              <app-main :full-screen="fullScreen" />
            </template>
          </app-tabs>
        </el-main>
      </el-container>
    </el-container>
  </el-container>

  <el-container v-else-if="appStore.layout === 'dock'">
    <el-header height="50px" style="border-bottom: 1px solid var(--el-border-color-lighter)">
      <app-header>
        <template #dock>
          <el-menu style="height: 50px" mode="horizontal" :default-active="oneLevel?.path">
            <el-menu-item v-for="item in menuTree" :key="item.name" :index="item.path" @click="openOneLevel(item)">
              <el-icon v-if="item.meta?.icon" :size="iconSizeNumber">
                <icon :name="item.meta?.icon" />
              </el-icon>
              <span class="title">{{ item.meta?.title }}</span>
            </el-menu-item>
          </el-menu>
        </template>
      </app-header>
    </el-header>
    <el-container>
      <el-aside v-show="oneLevel?.children?.length" :width="isMenuCollapse ? '64px' : '220px'">
        <app-menu v-model:collapse="isMenuCollapse" :menu-tree="oneLevel?.children" />
      </el-aside>
      <el-container>
        <el-main style="padding: 0">
          <app-tabs>
            <template #default="{ fullScreen }">
              <app-main :full-screen="fullScreen" />
            </template>
          </app-tabs>
        </el-main>
      </el-container>
    </el-container>
  </el-container>

  <el-container v-else-if="appStore.layout === 'topNav'">
    <el-header height="50px" style="border-bottom: 1px solid var(--el-border-color-lighter)">
      <app-header>
        <template #topNav>
          <app-menu :menu-tree="menuTree" mode="horizontal" style="justify-content: flex-end" />
        </template>
      </app-header>
    </el-header>
    <el-container>
      <el-main style="padding: 0">
        <app-tabs>
          <template #default="{ fullScreen }">
            <app-main :full-screen="fullScreen" />
          </template>
        </app-tabs>
      </el-main>
    </el-container>
  </el-container>

  <el-container v-else-if="appStore.layout === 'menu'">
    <el-header height="50px" style="border-bottom: 1px solid var(--el-border-color-lighter)">
      <app-header />
    </el-header>
    <el-container>
      <el-aside :width="isMenuCollapse ? '64px' : '220px'" class="aside-width">
        <app-menu v-model:collapse="isMenuCollapse" :menu-tree="menuTree" />
      </el-aside>
      <el-container>
        <el-main style="padding: 0">
          <app-tabs>
            <template #default="{ fullScreen }">
              <app-main :full-screen="fullScreen" />
            </template>
          </app-tabs>
        </el-main>
      </el-container>
    </el-container>
  </el-container>

  <app-setting />
</template>

<style scoped lang="scss">
.el-aside {
  transition: width 0.3s;
  -webkit-transition: width 0.3s;
}

.el-sub-menu.is-active,
.el-menu-item.is-active {
  background-color: var(--el-color-primary-light-8) !important;
}

.subfield {
  display: flex;

  .one-level {
    min-height: 100vh;
    border-right: 1px solid var(--el-border-color-lighter);

    // 分栏一级菜单配色调整 -- start
    background-color: #232f53;

    --el-menu-bg-color: rgba(0, 0, 0, 0.01);
    --el-menu-text-color: #f3f3f5;
    --el-menu-hover-color: rgba(120, 120, 120, 0.1);
    --el-menu-active-color: #f3f3f5;

    .el-sub-menu,
    .el-menu-item {
      &:hover {
        background-color: var(--el-menu-hover-color) !important;
      }
    }

    .el-sub-menu.is-active,
    .el-menu-item.is-active {
      background-color: var(--menu-active-background-color) !important;
    }

    // 分栏一级菜单配色调整 -- end （dark配色位于dark.scss）

    .one-level-logo {
      width: 70px;
      height: 50px;
      display: flex;
      align-items: center;
      flex-direction: column;
      justify-content: center;
    }

    .el-menu {
      width: 70px;
      margin-top: 10px;
      overflow-y: auto;
      height: calc(100vh - 60px);
      scrollbar-width: none; // 火狐
      -ms-overflow-style: none; // IE

      &::-webkit-scrollbar {
        // 谷歌  safari
        display: none;
      }

      .el-menu-item {
        height: 60px;
        padding-top: 5px;
        line-height: 15px;
        flex-direction: column;
      }
    }
  }

  .submenu {
    width: 100%;
    height: 100%;

    .title {
      height: 49px;
      padding: 0 20px;
      display: flex;
      align-items: center;
      border-bottom: 1px solid var(--el-border-color-lighter);
      border-right: 1px solid var(--el-border-color-lighter);
      font-size: var(--el-font-size-dynamic);
    }
  }
}

.mobile-menu-button {
  position: fixed;
  bottom: 20px;
  left: 20px;
  z-index: 10;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--el-color-primary);
  box-shadow: 0 2px 12px 0 var(--el-color-primary);
  border-radius: 50%;
  cursor: pointer;

  :deep(.el-drawer__body) {
    padding: 0;
  }
}

.default-menu {
  border: none;

  .default-menu-item {
    height: 70px !important;
    width: 70px;
    padding: 0 !important;
    display: flex;
    flex-direction: row;
    align-items: center;
    text-align: center;
    justify-content: center;

    .title {
      line-height: 24px;
      font-size: var(--el-font-size-dynamic);
      width: calc(100% - 10px);
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      padding: 0 5px;
    }
  }
}
</style>
