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

const menuTree = computed(() => getMenuTree())

const router = useRouter()

const submenu = ref<Array<RouteRecordRaw>>([])
const openOneLevel = (menu: RouteRecordRaw) => {
  submenu.value = menu.children?.length ? menu.children : []
  if (router.currentRoute.value.name !== menu.name) router.push({ name: menu.name })
}
onMounted(() => {
  openOneLevel(menuTree.value[0])
})
</script>

<template>
  <el-container v-if="appStore.layout === 'default'">
    <el-container>
      <el-aside :width="submenu.length ? '260px' : '71px'">
        <div class="subfield">
          <div class="one-level">
            <div class="one-level-logo">
              <img :src="Logo" alt="Logo" style="height: 39px" />
            </div>
            <div v-for="item in menuTree" :key="item.name" class="one-level-item" @click="openOneLevel(item)">
              <el-icon :size="30">
                <eleme />
              </el-icon>
              {{ item.meta?.title }}
            </div>
          </div>
          <div class="submenu">
            <div class="title">asd</div>
            <el-scrollbar v-show="submenu.length" height="calc(100vh - 50px)">
              <app-menu :menu-tree="submenu" />
            </el-scrollbar>
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
          <el-menu style="height: 50px" mode="horizontal">
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
      <el-aside v-show="submenu.length" width="220px">
        <el-scrollbar height="calc(100vh - 50px)">
          <app-menu :menu-tree="submenu" />
        </el-scrollbar>
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
      <el-aside width="220px">
        <el-scrollbar height="calc(100vh - 50px)">
          <app-menu :menu-tree="menuTree" />
        </el-scrollbar>
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
.subfield {
  display: flex;

  .one-level {
    min-height: 100vh;
    border-right: 1px solid var(--el-border-color-lighter);

    .one-level-logo,
    .one-level-item {
      width: 60px;
      height: 60px;
      margin: 5px;
      cursor: pointer;
      border-radius: 6px;
      display: flex;
      align-items: center;
      flex-direction: column;
      justify-content: center;
    }

    .one-level-item {
      color: var(--el-color-white);
      font-size: 13px;

      background-color: var(--el-color-primary);

      &:hover {
        background-color: var(--el-color-primary-light-3);
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
</style>
