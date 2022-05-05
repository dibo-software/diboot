<script setup lang="ts">
import AppHeader from './header/index.vue'
import AppMenu from './memu/index.vue'
import AppMain from './main/index.vue'
import AppFooter from './footer/index.vue'
import AppTabs from './tabs/index.vue'
import AppSetting from './setting/index.vue'

import { Eleme } from '@element-plus/icons-vue'

import Logo from '@/assets/logo.png'

import useAppStore from '@/store/app'
import { getMenuTree } from '@/utils/route'
import { RouteRecordRaw } from 'vue-router'

const appStore = useAppStore()

const menuTree = getMenuTree()

const router = useRouter()
const oneLevel = ref<RouteRecordRaw>()
const openOneLevel = (menu: RouteRecordRaw) => {
  oneLevel.value = menu
  if (router.currentRoute.value.name !== menu.name) router.push({ name: menu.name })
}
openOneLevel(menuTree[0])

const isMenuCollapse = ref(false)

watch(
  () => router.currentRoute.value,
  value => {
    // 存在redirect，且存在title（（上级菜单menu中显示的情况））
    // 或
    // 路径匹配（上级菜单menu中不显示的情况）
    oneLevel.value = value.matched.find(item => (item.redirect && item.meta.title) || item.path === value.path)
  }
)
</script>

<template>
  <el-container v-if="appStore.layout === 'default'">
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
                @click="openOneLevel(item)"
              >
                <el-icon :size="30">
                  <eleme />
                </el-icon>
                <span class="title">{{ item.meta?.title }}</span>
              </el-menu-item>
            </el-menu>
          </div>
          <div v-show="oneLevel?.children?.length" class="submenu">
            <app-menu v-model:collapse="isMenuCollapse" :menu-tree="oneLevel?.children">
              <template #title>
                <div class="title">{{ oneLevel?.meta?.title }}</div>
              </template>
            </app-menu>
          </div>
        </div>
      </el-aside>
      <el-container>
        <el-main style="padding: 0">
          <el-header height="50px" style="border-bottom: 1px solid #eee">
            <app-header />
          </el-header>
          <app-tabs>
            <template #default="{ fullScreen }">
              <app-main :full-screen="fullScreen">
                <template #footer>
                  <app-footer />
                </template>
              </app-main>
            </template>
          </app-tabs>
        </el-main>
      </el-container>
    </el-container>
  </el-container>

  <el-container v-if="appStore.layout === 'dock'">
    <el-header height="50px" style="border-bottom: 1px solid #eee">
      <app-header>
        <template #dock>
          <el-menu style="height: 50px" mode="horizontal" :default-active="oneLevel?.path">
            <el-menu-item v-for="item in menuTree" :key="item.name" :index="item.path" @click="openOneLevel(item)">
              <el-icon :size="22">
                <eleme />
              </el-icon>
              <span>{{ item.meta?.title }}</span>
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
              <app-main :full-screen="fullScreen">
                <template #footer>
                  <app-footer />
                </template>
              </app-main>
            </template>
          </app-tabs>
        </el-main>
      </el-container>
    </el-container>
  </el-container>

  <el-container v-if="appStore.layout === 'topNav'">
    <el-header height="50px" style="border-bottom: 1px solid #eee">
      <app-header>
        <template #topNav>
          <app-menu :menu-tree="menuTree" mode="horizontal" />
        </template>
      </app-header>
    </el-header>
    <el-container>
      <el-main style="padding: 0">
        <app-tabs>
          <template #default="{ fullScreen }">
            <app-main :full-screen="fullScreen">
              <template #footer>
                <app-footer />
              </template>
            </app-main>
          </template>
        </app-tabs>
      </el-main>
    </el-container>
  </el-container>

  <el-container v-if="appStore.layout === 'menu'">
    <el-header height="50px" style="border-bottom: 1px solid #eee">
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
              <app-main :full-screen="fullScreen">
                <template #footer>
                  <app-footer />
                </template>
              </app-main>
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

.subfield {
  display: flex;

  .one-level {
    min-height: 100vh;
    border-right: 1px solid var(--el-border-color-lighter);

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
    }
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
      font-size: 14px;
    }
  }
}
</style>
