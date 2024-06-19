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
  <el-dialog v-model="visible" :title="$t('title.detail')" width="65vw">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item :label="$t('operationLog.userRealname')">
        {{ model.userRealname }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.userTypeAndId')">
        <span>{{ model.userType }} : {{ model.userId }}</span>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.businessObj')">
        {{ model.businessObj }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.operation')">
        {{ model.operation }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.requestUri')">
        <span>{{ model.requestMethod }} : {{ model.requestUri }}</span>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.requestIp')">
        {{ model.requestIp }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.statusCode')">
        <el-tag v-if="model.statusCode === 0">{{ model.statusCode }}</el-tag>
        <el-tag v-else type="danger">{{ model.statusCode }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.createTime')">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.requestParams')" :span="2" class-name="long-text">
        {{ model.requestParams }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.errorMsg')" :span="2" class-name="long-text">
        {{ model.errorMsg }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.close') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
