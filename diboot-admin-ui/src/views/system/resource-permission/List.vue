<script setup lang="ts" name="ResourcePermission">
import ResourcePermissionForm from './Form.vue'
import MenuTree from './modules/MenuTree.vue'
import type { ResourcePermission } from '@/views/system/resource-permission/type'
import { getElementHeight } from '@/utils/document'
import type { Ref } from 'vue'

const formValue = ref<Partial<ResourcePermission>>({})
const clickNode = (node: ResourcePermission) => {
  formValue.value = node
}
const boxHeight = ref(0)
nextTick(() => {
  boxHeight.value = getElementHeight('.menu-permission-container')
})
// 多层组件传递 boxHeight
provide<Ref<number>>('boxHeight', boxHeight)
</script>
<template>
  <el-container class="menu-permission-container">
    <el-aside class="menu-aside-container">
      <menu-tree ref="menuTreeRef" @click-node="clickNode" />
    </el-aside>
    <el-main class="menu-main-container">
      <resource-permission-form :form-value="formValue" @complete="(id: string) => $refs.menuTreeRef.refresh(id)" />
    </el-main>
  </el-container>
</template>
<style scoped lang="scss">
.menu-permission-container {
  height: 100%;
}
.menu-aside-container {
  border-right: 1px solid var(--el-border-color-lighter);
}
.menu-main-container {
  padding: 0;
}
</style>
