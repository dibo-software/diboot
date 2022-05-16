<script setup lang="ts">
import SubMenu from './subMenu.vue'
import { RouteRecordRaw } from 'vue-router'

import { Fold, Expand } from '@element-plus/icons-vue'

const props = withDefaults(
  defineProps<{ menuTree: RouteRecordRaw[]; collapse?: boolean; mode?: 'horizontal' | 'vertical'; height?: string }>(),
  { menuTree: () => [], mode: 'vertical' }
)

const emits = defineEmits<{ (e: 'update:collapse', collapse: boolean): void }>()

const toggleState = () => emits('update:collapse', !props.collapse)
</script>

<template>
  <el-menu v-if="mode === 'horizontal'" router :default-active="$route.path" mode="horizontal">
    <sub-menu :menu-tree="menuTree" />
  </el-menu>
  <el-scrollbar v-else-if="height" :height="height">
    <el-menu router :default-active="$route.path" :style="{ minHeight: height }">
      <sub-menu :menu-tree="menuTree" />
    </el-menu>
  </el-scrollbar>
  <div v-else>
    <span v-show="!collapse">
      <slot name="title" />
    </span>
    <el-scrollbar :height="collapse && $slots.title ? 'calc(100vh - 36px)' : 'calc(100vh - 86px)'">
      <el-menu
        router
        :default-active="$route.path"
        :collapse="collapse"
        :style="{ minHeight: collapse && $slots.title ? 'calc(100vh - 36px)' : 'calc(100vh - 86px)' }"
      >
        <sub-menu :menu-tree="menuTree" />
      </el-menu>
    </el-scrollbar>
    <div class="collapse" @click="toggleState()">
      <el-icon :size="20">
        <expand v-if="collapse" />
        <fold v-else />
      </el-icon>
    </div>
  </div>
</template>

<style scoped lang="scss">
.collapse {
  height: 35px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-top: 1px solid var(--el-border-color-lighter);

  &:hover {
    color: var(--el-color-primary);
  }
}
</style>
