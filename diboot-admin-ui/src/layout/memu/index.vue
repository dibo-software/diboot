<script setup lang="ts">
import SubMenu from './subMenu.vue'
import { getMenuTree } from '@/utils/route'
</script>

<template>
  <el-menu router :default-active="$route.path">
    <sub-menu :menu-tree="getMenuTree()" />
  </el-menu>
</template>
