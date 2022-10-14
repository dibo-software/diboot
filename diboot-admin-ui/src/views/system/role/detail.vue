<script setup lang="ts" name="RoleDetail">
import type { Role } from './type'
import type { ResourcePermission } from '@/views/system/resource-permission/type'
import { Folder, Menu, Link, Connection, Key } from '@element-plus/icons-vue'
const { loadData, loading, model } = useDetail<Role>('/role')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})

// 权限树相关
const transformField = {
  label: 'displayName'
}
const { treeRef, treeDataList, getTree, flatTreeNodeClass } = useTreeCrud<ResourcePermission>({
  baseApi: '/resource-permission',
  treeApi: '/list',
  transformField
})
const treeProps = {
  label: 'displayName',
  class: flatTreeNodeClass
}
getTree()
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
  <el-dialog v-model="visible" title="详情" width="65vw">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item label="名称">
        {{ model.name }}
      </el-descriptions-item>
      <el-descriptions-item label="编码">
        {{ model.code }}
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">
        {{ model.description }}
      </el-descriptions-item>
      <el-descriptions-item label="授权权限" :span="2">
        <el-scrollbar height="400px">
          <el-tree
            ref="treeRef"
            style="width: 100%"
            :expand-on-click-node="false"
            :props="treeProps"
            :data="treeDataList"
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
      <el-descriptions-item label="创建时间">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        {{ model.updateTime }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
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
