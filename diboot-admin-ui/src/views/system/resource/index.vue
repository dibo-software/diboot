<script setup lang="ts" name="Resource">
import ResourceTree from './Tree.vue'
import ResourceForm from './Form.vue'
import type { Resource } from './type'

import MobileList from './mobile/List.vue'

const formValue = ref<Resource>()
const clickNode = (node: Resource) => {
  formValue.value = node
}

const menuTreeRef = ref()

const type = ref('PC')
</script>

<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <div :key="type" style="margin: 5px 5px 0">
      <el-button round size="default" type="success" :plain="type !== 'PC'" @click="type = 'PC'">
        PC 菜单管理
      </el-button>
      <el-button round size="default" type="warning" :plain="type !== 'mobile'" @click="type = 'mobile'">
        移动端菜单管理
      </el-button>
    </div>

    <MobileList v-if="type === 'mobile'" />

    <div v-else style="height: calc(100% - 39px); flex: 1; display: flex">
      <resource-tree ref="menuTreeRef" style="width: 260px" @click-node="clickNode" />
      <resource-form
        :form-value="formValue"
        lend-height="36px"
        style="flex: 1"
        @complete="(id: string) => menuTreeRef?.refresh(id)"
      />
    </div>
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
