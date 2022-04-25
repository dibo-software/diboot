<script setup lang="ts">
import useViewTabsStore from '@/store/viewTabs'
import { RouteLocationNormalized } from 'vue-router'

const viewTabsStore = useViewTabsStore()

const slotFooter = !!useSlots().footer

// 计算已占用高度
const alreadyOccupyHeight = (route: RouteLocationNormalized) =>
  // header + tabs + padding + bounded-padding + footer
  50 + 36 + 20 + (route.meta.hollow ? 0 : 20) + (slotFooter && !route.meta.hideFooter ? 50 + 2 : 0)

// 获取自定义内容的 class
const customContentClass = (route: RouteLocationNormalized) => {
  if (route.meta.hollow) return slotFooter && !route.meta.hideFooter ? 'hollow' : ''
  return slotFooter && !route.meta.hideFooter ? 'bounded-footer' : 'bounded'
}
</script>

<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="route.meta.transition" mode="out-in">
      <keep-alive :include="viewTabsStore.cachedViews">
        <div :key="route.fullPath" class="content">
          <div :class="customContentClass(route)">
            <component :is="Component" :already-occupy-height="alreadyOccupyHeight(route)" />
          </div>
          <div v-if="slotFooter && !route.meta.hideFooter" class="footer">
            <slot name="footer" />
          </div>
        </div>
      </keep-alive>
    </transition>
  </router-view>
</template>

<style scoped lang="scss">
.content {
  padding: 10px;
  background-color: rgba(125, 125, 125, 0.1);
  min-height: calc(100vh - 106px);

  .hollow {
    min-height: calc(100vh - 158px);
  }

  .bounded,
  .bounded-footer {
    padding: 10px 15px;
    background-color: #fff;
  }

  .bounded {
    border-radius: 5px;
    min-height: calc(100vh - 126px);
  }

  .bounded-footer {
    min-height: calc(100vh - 178px);
    border-radius: 5px 5px 0 0;
  }

  .footer {
    height: 50px;
    color: darkgray;
    display: flex;
    align-items: center;
    justify-content: space-around;
    border-top: 2px dashed #eeeeee;
    border-radius: 0 0 5px 5px;
    background-color: #fff;
  }
}
</style>
