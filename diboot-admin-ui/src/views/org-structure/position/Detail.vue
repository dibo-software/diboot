<script setup lang="ts">
import type { Position } from './type'

const { loadData, loading, model } = useDetail<Position>('/iam/position')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
</script>

<template>
  <el-dialog v-model="visible" :width="720" title="详情">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item label="名称">
        {{ model.name }}
      </el-descriptions-item>
      <el-descriptions-item label="编码">
        {{ model.code }}
      </el-descriptions-item>
      <el-descriptions-item label="职级">
        {{ model.gradeName }}
      </el-descriptions-item>
      <el-descriptions-item label="数据权限">
        {{ model.dataPermissionTypeLabel }}
      </el-descriptions-item>
      <el-descriptions-item label="虚拟岗位">
        {{ model.isVirtual ? '是' : '否' }}
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
