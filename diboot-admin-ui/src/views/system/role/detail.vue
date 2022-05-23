<script setup lang="ts" name="RoleDetail">
import type { Role } from './type'

const { loadData, loading, model } = useDetailDefault<Role>('/role')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
</script>

<template>
  <el-dialog v-model="visible" title="详情" width="65vw">
    <el-descriptions v-loading="loading" :column="2">
      <el-descriptions-item label="名称">
        {{ model.name }}
      </el-descriptions-item>
      <el-descriptions-item label="编码">
        {{ model.code }}
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">
        {{ model.description }}
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

<style scoped></style>
