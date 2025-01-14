<script setup lang="ts">
import type { OperationLog } from './type'
import { useI18n } from 'vue-i18n'
import useClipboard from 'vue-clipboard3'

const { model, loadData, loading } = useDetail<OperationLog>('/iam/operation-log')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})

const i18n = useI18n()

const clipboard = useClipboard()

const clipboardWrit = (val = '') =>
  clipboard
    .toClipboard(val.toString())
    .then(() => ElMessage.success(i18n.t('copy.success')))
    .catch(() => ElMessage.error(i18n.t('copy.error')))
</script>

<template>
  <el-dialog v-model="visible" :title="$t('title.detail')" width="65vw" draggable>
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item :label="$t('operationLog.userRealname')" label-class-name="item-label">
        {{ model.userRealname }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.userTypeAndId')" label-class-name="item-label">
        <span>{{ model.userType }} : {{ model.userId }}</span>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.businessObj')" label-class-name="item-label">
        {{ model.businessObj }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.operation')" label-class-name="item-label">
        {{ model.operation }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.requestUri')" label-class-name="item-label">
        <span>{{ model.requestMethod }} : {{ model.requestUri }}</span>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.requestIp')" label-class-name="item-label">
        {{ model.requestIp }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.statusCode')" label-class-name="item-label">
        <el-tag v-if="model.statusCode === 0">{{ model.statusCode }}</el-tag>
        <el-tag v-else type="danger">{{ model.statusCode }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('operationLog.createTime')" label-class-name="item-label">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item
        :label="$t('operationLog.requestParams')"
        :span="2"
        class-name="long-text"
        label-class-name="item-label"
      >
        {{ model.requestParams }}
      </el-descriptions-item>
      <el-descriptions-item
        :label="$t('operationLog.errorMsg')"
        :span="2"
        class-name="long-text"
        label-class-name="item-label"
      >
        <el-button
          v-if="model.errorMsg"
          link
          type="primary"
          style="position: absolute; right: 15px; z-index: 9; margin-top: -12px"
          @click="clipboardWrit(model.errorMsg)"
        >
          {{ $t('copy.label') }}
        </el-button>
        <el-scrollbar max-height="300px" style="white-space: pre-line">
          {{ model.errorMsg }}
        </el-scrollbar>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.close') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-descriptions {
  :deep(.item-label) {
    width: 150px;
  }
}
</style>
