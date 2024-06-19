<script setup lang="ts">
import Dashboard from '@/components/icon/tabbar/Dashboard.vue'
import DashboardActive from '@/components/icon/tabbar/DashboardActive.vue'
import Stacking from '@/components/icon/tabbar/Stacking.vue'
import StackingActive from '@/components/icon/tabbar/StackingActive.vue'
import Mine from '@/components/icon/tabbar/Mine.vue'
import MineActive from '@/components/icon/tabbar/MineActive.vue'
import useAuthStore from '@/stores/auth'

useAuthStore().getInfo()
const route = useRoute()
const firstLevelPath = route.fullPath.split('/')[1]
console.log(firstLevelPath)
const active = ref<string>(
  firstLevelPath === 'mine' ? 'Mine' : firstLevelPath === 'business' ? 'Component' : 'Dashboard'
)
</script>

<template>
  <van-sticky>
    <van-nav-bar :left-arrow="!$route.meta?.hideBack" :title="$route.meta?.title" @click-left="$router.back()" />
  </van-sticky>

  <div
    :style="{
      height: 'calc(100vh - ' + ($route.meta?.showTabbar ? 96 : 46) + 'px)',
      overflowY: 'auto',
      backgroundColor: 'var(--van-gray-1)'
    }"
  >
    <transition name="custom" mode="in-out">
      <RouterView />
    </transition>
  </div>

  <van-tabbar v-model="active" v-show="$route.meta?.showTabbar">
    <van-tabbar-item name="Dashboard" icon="home-o" @click="$router.push({ name: 'Dashboard' })">
      <template #icon>
        <Icon>
          <component :is="active === 'Dashboard' ? DashboardActive : Dashboard" />
        </Icon>
      </template>
      {{ $t('layout.dashboard') }}
    </van-tabbar-item>
    <van-tabbar-item name="Component" icon="apps-o" @click="$router.push({ name: 'Crud' })">
      <template #icon>
        <icon size="var(--van-tabbar-item-icon-size)">
          <component :is="active === 'Component' ? StackingActive : Stacking" />
        </icon>
      </template>
      {{ $t('layout.example') }}
    </van-tabbar-item>
    <van-tabbar-item name="Mine" icon="contact" @click="$router.push({ name: 'Mine' })">
      <template #icon>
        <icon size="var(--van-tabbar-item-icon-size)">
          <component :is="active === 'Mine' ? MineActive : Mine" />
        </icon>
      </template>
      {{ $t('layout.mine') }}
    </van-tabbar-item>
  </van-tabbar>
</template>
<style>
.custom-enter-active,
.custom-leave-active {
  transition: all 0.25s ease-out;
}

.custom-enter-from {
  opacity: 0;
  transform: translateY(30px);
}

.custom-leave-to {
  opacity: 0;
  transform: translateY(-30px);
}
</style>
