<script setup lang="ts">
import SubMenu from './subMenu.vue'
import { RouteRecordRaw } from 'vue-router'

defineProps<{ menuTree: RouteRecordRaw[]; mode?: 'horizontal' }>()
</script>

<template>
  <el-menu router :default-active="$route.path" :mode="mode">
    <sub-menu :menu-tree="menuTree" />
  </el-menu>
</template>
