<script setup lang="ts">
import useViewTabsStore from '@/store/viewTabs'

const viewTabsStore = useViewTabsStore()
</script>

<template>
  <router-view v-slot="{ Component, route }">
    <transition :name="route.meta.transition" mode="out-in">
      <keep-alive :include="viewTabsStore.cachedViews">
        <div :key="route.fullPath" class="content">
          <div :class="route.meta.hollow ? '' : 'bounded'">
            <component :is="Component" />
          </div>
          <slot v-if="!route.meta.hideFooter" name="footer" />
        </div>
      </keep-alive>
    </transition>
  </router-view>
</template>

<style scoped>
.content {
  padding: 10px;
  background-color: rgba(125, 125, 125, 0.1);
}

.content > .bounded {
  padding: 10px 15px;
  border-radius: 5px 5px 0 0;
  background-color: #fff;
}
</style>
