<script setup lang="ts">
import type { OperationLog } from './type'

const { model, loadData, loading } = useDetail<OperationLog>('/iam/operation-log')

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
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item label="用户姓名">
        {{ model.userRealname }}
      </el-descriptions-item>
      <el-descriptions-item label="用户类型:ID">
        <span>{{ model.userType }} : {{ model.userId }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="业务对象">
        {{ model.businessObj }}
      </el-descriptions-item>
      <el-descriptions-item label="操作事项">
        {{ model.operation }}
      </el-descriptions-item>
      <el-descriptions-item label="请求URL">
        <span>{{ model.requestMethod }} : {{ model.requestUri }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="客户端IP">
        {{ model.requestIp }}
      </el-descriptions-item>
      <el-descriptions-item label="状态码">
        <el-tag v-if="model.statusCode === 0">{{ model.statusCode }}</el-tag>
        <el-tag v-else type="danger">{{ model.statusCode }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="操作时间">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item label="请求参数" :span="2" class-name="long-text">
        {{ model.requestParams }}
      </el-descriptions-item>
      <el-descriptions-item label="错误信息" :span="2" class-name="long-text">
        {{ model.errorMsg }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
