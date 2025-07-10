<script setup lang="ts">
import type { ScheduleJobLog } from '../type'

const { loadData, loading, model } = useDetail<ScheduleJobLog>('/schedule-job/log')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
</script>

<template>
  <el-drawer v-model="visible" :title="$t('title.detail')" size="650px">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item :label="$t('scheduleJobLog.startTime')">
        {{ model.startTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('scheduleJobLog.endTime')">
        {{ model.endTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('scheduleJobLog.runStatus')">
        <el-tag v-if="model.runStatus === 'S'">{{ $t('scheduleJobLog.success') }}</el-tag>
        <el-tag v-else type="danger">{{ $t('scheduleJobLog.fail') }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('scheduleJobLog.triggerMode')">
        {{ model.triggerModeLabel }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('scheduleJobLog.elapsedSeconds')">
        {{ model.elapsedSeconds }} s
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createTime')">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('scheduleJobLog.paramJson')" :span="2" class-name="long-text">
        {{ model.paramJson }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('scheduleJobLog.executeMsg')" :span="2" class-name="long-text">
        {{ model.executeMsg }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.close') }}</el-button>
    </template>
  </el-drawer>
</template>

<style scoped lang="scss"></style>
