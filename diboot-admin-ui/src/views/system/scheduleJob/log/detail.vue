<script setup lang="ts" name="RoleDetail">
import type { ScheduleJobLog } from '../type'

const { loadData, loading, model } = useDetailDefault<ScheduleJobLog>('/scheduleJob/log')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
</script>

<template>
  <el-drawer v-model="visible" title="日志详情" size="650px">
    <el-descriptions v-loading="loading" :column="2">
      <el-descriptions-item label="开始时间">
        {{ model.startTime }}
      </el-descriptions-item>
      <el-descriptions-item label="结束时间">
        {{ model.endTime }}
      </el-descriptions-item>
      <el-descriptions-item label="执行状态">
        <el-tag v-if="model.runStatus">成功</el-tag>
        <el-tag v-else type="danger">失败</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="触发方式">
        {{ model.triggerModeLabel }}
      </el-descriptions-item>
      <el-descriptions-item label="耗时"> {{ model.elapsedSeconds }} s </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ model.createTime }}
      </el-descriptions-item>
      <el-descriptions-item label="参数" :span="3">
        <p class="content">{{ model.paramJson }}</p>
      </el-descriptions-item>
      <el-descriptions-item label="执行结果信息" :span="3">
        <p class="content">{{ model.executeMsg }}</p>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-drawer>
</template>

<style scoped lang="scss">
.content {
  word-wrap: break-word;
  word-break: break-all;
}
</style>
