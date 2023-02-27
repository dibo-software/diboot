<script setup lang="ts" name="Resource">
import ResourceTree from './Tree.vue'
import ResourceForm from './Form.vue'
import type { Resource } from './type'

const formValue = ref<Resource>()
const clickNode = (node: Resource) => {
  formValue.value = node
}

const menuTreeRef = ref()
</script>

<template>
  <div style="height: 100%; display: flex">
    <resource-tree ref="menuTreeRef" style="width: 260px" @click-node="clickNode" />
    <resource-form :form-value="formValue" style="flex: 1" @complete="(id: string) => menuTreeRef?.refresh(id)" />
  </div>
</template>

<style scoped lang="scss">
.menu-permission-container {
  height: 100%;

  .menu-aside-container {
    border-right: 1px solid var(--el-border-color-lighter);
  }

  .menu-main-container {
    padding: 0;
  }
}
</style>
