<script setup lang="ts">
import { RouteRecordRaw } from 'vue-router'
import { Eleme } from '@element-plus/icons-vue'

defineProps<{ menuTree: RouteRecordRaw[] }>()
</script>

<template>
  <template v-for="(item, index) in menuTree" :key="index">
    <el-sub-menu v-if="item.children?.length" :index="item.path">
      <template #title>
        <el-icon :size="22">
          <eleme />
        </el-icon>
        <span>{{ item.meta?.title }}</span>
      </template>
      <sub-menu :menu-tree="item.children" />
    </el-sub-menu>
    <el-menu-item v-else :index="item.path">
      <el-icon :size="22">
        <eleme />
      </el-icon>
      <span>{{ item.meta?.title }}</span>
    </el-menu-item>
  </template>
</template>

<style scoped lang="scss">
.el-menu-item.is-active {
  background-color: var(--el-color-primary-light-9);
}
</style>
