<script setup lang="ts">
import type { Role } from './type'
import type { Resource } from '@/views/system/resource/type'
import { Folder, Menu, Link, Connection, Key } from '@element-plus/icons-vue'
import type { TreeNodeData } from 'element-plus/es/components/tree/src/tree.type'

const { loadData, loading, model } = useDetail<Role & { permissionVOList?: Resource[] }>('/iam/role')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})

/**
 * 扁平化树最后一组节点
 * @param data
 */
const flatTreeNodeClass = (data: TreeNodeData) => {
  const falseVal = { 'flat-tree-node-container': false }
  const children = (data.children ?? []) as Record<string, unknown>[]
  if (!children || children.length === 0) return falseVal
  // 检查子节点是否是最后一组
  for (const child of children) {
    const temp = (child.children ?? []) as Record<string, unknown>[]
    if (temp && temp.length !== 0) return falseVal
  }
  return { 'flat-tree-node-container': true }
}

const treeProps = {
  label: 'displayName',
  class: flatTreeNodeClass
}

const iconMap = {
  CATALOGUE: Folder,
  MENU: Menu,
  OUTSIDE_URL: Connection,
  IFRAME: Link,
  PERMISSION: Key
}

const getIcon = (val: string) => {
  return Object.getOwnPropertyDescriptor(iconMap, val)?.value
}
</script>

<template>
  <el-dialog v-model="visible" :title="$t('title.detail')" width="65vw" top="10vh">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item :label="$t('role.name')">
        {{ model.name }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('role.code')">
        {{ model.code }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('role.description')" :span="2">
        {{ model.description }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('role.grantPermission')" :span="2">
        <el-scrollbar height="calc(80vh - 300px)">
          <el-tree
            style="width: 100%"
            :expand-on-click-node="false"
            :props="treeProps"
            :data="model.permissionVOList"
            node-key="id"
            default-expand-all
          >
            <template #default="{ node, data }">
              <span style="display: flex; align-items: center">
                <el-icon>
                  <component :is="getIcon(data.displayType)" />
                </el-icon>
                <span>{{ node.label }} </span>
              </span>
            </template>
          </el-tree>
        </el-scrollbar>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createTime')">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.updateTime')">
        {{ model.updateTime }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.close') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.el-tag:first-child {
  margin-right: 5px;
}
.el-tag + .el-tag {
  margin-right: 5px;
  margin-bottom: 5px;
}
</style>
