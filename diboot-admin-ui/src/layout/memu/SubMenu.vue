<script setup lang="ts">
import type { RouteRecordRaw } from 'vue-router'
import { iconSizeNumber } from '@/utils/theme'

defineProps<{ menuTree: RouteRecordRaw[]; collapse?: boolean }>()
</script>

<template>
  <template v-for="(item, index) in menuTree" :key="index">
    <el-sub-menu v-if="item.children?.length" :index="item.path">
      <template #title>
        <el-icon v-if="item.meta?.icon" :size="iconSizeNumber">
          <icon :name="item.meta?.icon" />
        </el-icon>
        <span class="menu-text">{{ item.meta?.title }}</span>
      </template>
      <sub-menu :menu-tree="item.children" />
    </el-sub-menu>
    <el-tooltip v-else :disabled="!collapse" :content="item.meta?.title" placement="right" :show-after="200">
      <el-menu-item :index="item.path" style="width: 188px">
        <el-icon v-if="item.meta?.icon" :size="iconSizeNumber">
          <icon :name="item.meta?.icon" />
        </el-icon>
        <span class="menu-text">{{ item.meta?.title }}</span>
      </el-menu-item>
    </el-tooltip>
  </template>
</template>

<style scoped lang="scss">
.el-menu-item.is-active {
  background-color: var(--el-color-primary-light-8);
}
.menu-text {
  font-size: var(--el-font-size-dynamic);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
