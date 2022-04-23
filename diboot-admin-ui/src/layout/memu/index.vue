<script setup lang="ts">
import SubMenu from './subMenu.vue'
import { buildMenuTree } from '@/utils/route'

const menuTree = buildMenuTree(useRouter().getRoutes())
</script>

<template>
  <el-menu router>
    <sub-menu :menu-tree="menuTree" />
  </el-menu>
</template>
