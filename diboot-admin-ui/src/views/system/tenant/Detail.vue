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
  <el-dialog v-model="visible" :width="720" title="详情">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item label="租户名称">
        {{ model.name }}
      </el-descriptions-item>
      <el-descriptions-item label="租户编码">
        {{ model.code }}
      </el-descriptions-item>
      <el-descriptions-item label="有效开始日期">
        {{ model.startDate }}
      </el-descriptions-item>
      <el-descriptions-item label="有效结束日期">
        {{ model.endDate }}
      </el-descriptions-item>
      <el-descriptions-item label="负责人">
        {{ model.manager }}
      </el-descriptions-item>
      <el-descriptions-item label="联系电话">
        {{ model.phone }}
      </el-descriptions-item>
      <el-descriptions-item label="租户状态">
        <el-tag
          v-if="(model.statusLabel as LabelValue)?.value"
          :color="(model.statusLabel as LabelValue<{ color: string }>).ext?.color"
          effect="dark"
          type="info"
        >
          {{ (model.statusLabel as LabelValue).label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="创建人">
        {{ model.createByLabel }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        {{ model.updateTime }}
      </el-descriptions-item>
      <el-descriptions-item label="描述">
        {{ model.description }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss"></style>
