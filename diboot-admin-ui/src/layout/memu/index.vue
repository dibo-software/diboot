<script setup lang="ts">
import SubMenu from './subMenu.vue'
import useAsyncRouteStore from '@/store/asyncRoute'

const asyncRouteStore = useAsyncRouteStore()
</script>
<template>
  <el-menu router>
    <SubMenu :menu-list="asyncRouteStore.menus" />
  </el-menu>
</template>
