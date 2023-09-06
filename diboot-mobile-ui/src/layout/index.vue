<script setup lang="ts">
import Home from '@/components/icon/tabbar/Home.vue'
import HomeActive from '@/components/icon/tabbar/HomeActive.vue'
import Stacking from '@/components/icon/tabbar/Stacking.vue'
import StackingActive from '@/components/icon/tabbar/StackingActive.vue'
import Mine from '@/components/icon/tabbar/Mine.vue'
import MineActive from '@/components/icon/tabbar/MineActive.vue'
import useAuthStore from '@/stores/auth'

useAuthStore().getInfo()
const route = useRoute()
const firstLevelPath = route.fullPath.split('/')[1]
const active = ref<string>(firstLevelPath === 'mine' ? 'Mine' : firstLevelPath === 'business' ? 'Component' : 'Home')
</script>

<template>
  <van-sticky>
    <van-nav-bar :left-arrow="!$route.meta?.hideBack" :title="$route.meta?.title" @click-left="$router.back()" />
  </van-sticky>

  <div :style="{ height: 'calc(100vh - ' + ($route.meta?.showTabbar ? 96 : 50) + 'px)', overflowY: 'auto' }">
    <RouterView />
  </div>

  <van-tabbar v-model="active" v-show="$route.meta?.showTabbar">
    <van-tabbar-item name="Home" icon="home-o" @click="$router.push({ name: 'Dashboard' })">
      <template #icon>
        <Icon>
          <component :is="active === 'Home' ? HomeActive : Home" />
        </Icon>
      </template>
      首页
    </van-tabbar-item>
<!--    <van-tabbar-item name="Component" icon="apps-o" @click="$router.push({ name: 'Business' })">-->
<!--      <template #icon>-->
<!--        <icon size="var(&#45;&#45;van-tabbar-item-icon-size)">-->
<!--          <component :is="active === 'Component' ? StackingActive : Stacking" />-->
<!--        </icon>-->
<!--      </template>-->
<!--      组件-->
<!--    </van-tabbar-item>-->
    <van-tabbar-item name="Mine" icon="contact" @click="$router.push({ name: 'Mine' })">
      <template #icon>
        <icon size="var(--van-tabbar-item-icon-size)">
          <component :is="active === 'Mine' ? MineActive : Mine" />
        </icon>
      </template>
      我的
    </van-tabbar-item>
  </van-tabbar>
</template>
