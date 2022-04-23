<script setup lang="ts">
import { RouteRecordRaw } from 'vue-router'

defineProps<{ menuTree: RouteRecordRaw[] }>()
</script>

<template>
  <template v-for="(item, index) in menuTree" :key="index">
    <el-sub-menu v-if="item.children?.length" :index="item.path">
      <template #title>
        图标：
        <span>{{ item.meta.title }}</span>
      </template>
      <sub-menu :menu-tree="item.children" />
    </el-sub-menu>
    <el-menu-item v-else :index="item.path">
      图标：
      <span>{{ item.meta.title }}</span>
    </el-menu-item>
  </template>
</template>
