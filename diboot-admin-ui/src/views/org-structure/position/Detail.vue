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
  <el-dialog v-model="visible" :width="720" :title="$t('title.detail')">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item :label="$t('position.name')">
        {{ model.name }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('position.code')">
        {{ model.code }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('position.gradeName')">
        {{ model.gradeName }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('position.dataPermissionType')">
        <el-tag :color="model.dataPermissionTypeLabel?.ext?.color" effect="dark" type="info">
          {{ model.dataPermissionTypeLabel?.label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('position.isVirtual')">
        {{ model.isVirtual ? $t('bool.yes') : $t('bool.no') }}
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

<style scoped></style>
