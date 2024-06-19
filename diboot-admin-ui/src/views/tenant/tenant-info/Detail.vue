<script setup lang="ts">
import type { Tenant } from './type'

const { loadData, loading, model } = useDetail<Tenant>('/iam/tenant')

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
      <el-descriptions-item :label="$t('tenantInfo.name')">
        {{ model.name }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('tenantInfo.code')">
        {{ model.code }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('tenantInfo.startDate')">
        {{ model.startDate }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('tenantInfo.endDate')">
        {{ model.endDate }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('tenantInfo.manager')">
        {{ model.manager }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('tenantInfo.phone')">
        {{ model.phone }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('tenantInfo.status')">
        <el-tag
          v-if="(model.statusLabel as LabelValue)?.value"
          :color="(model.statusLabel as LabelValue<{ color: string }>).ext?.color"
          effect="dark"
          type="info"
        >
          {{ (model.statusLabel as LabelValue).label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createBy')">
        {{ model.createByLabel }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createTime')">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.updateTime')">
        {{ model.updateTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('tenantInfo.description')">
        {{ model.description }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.close') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss"></style>
