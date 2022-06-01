<script setup lang="ts">
import ResourcePermissionForm from './form.vue'
import MenuTree from './modules/MenuTree.vue'
import { ResourcePermission } from '@/views/system/resourcePermission/type'
// 接收可视高度
defineProps<{ usedVisibleHeight?: number }>()
const formValue = ref<Partial<ResourcePermission>>({})
const clickNode = (node: ResourcePermission) => {
  console.log(node)
  formValue.value = node
}
</script>
<template>
  <el-container class="menu-permission-container">
    <el-aside class="menu-aside-container">
      <menu-tree @click-node="clickNode" />
    </el-aside>
    <el-main class="menu-main-container">
      <resource-permission-form :form-value="formValue" />
    </el-main>
  </el-container>
</template>
<style scoped lang="scss">
.menu-permission-container {
  height: calc(100vh - v-bind('usedVisibleHeight + "px"'));
}
.menu-aside-container {
  border-right: 1px solid var(--el-color-info-light-9);
  padding-right: 5px;
}
.menu-main-container {
  padding: 10px;
}
</style>
