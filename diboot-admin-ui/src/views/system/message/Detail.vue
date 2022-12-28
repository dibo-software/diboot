<script setup lang="ts">
import type { Message } from './type'

const { model, loadData, loading } = useDetail<Message>('/message')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
</script>

<template>
  <el-dialog v-model="visible" title="消息通知详情" width="65vw">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item label="业务类型">
        {{ model.businessType }}
      </el-descriptions-item>
      <el-descriptions-item label="业务标识">
        <span>{{ model.businessCode }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="标题">
        {{ model.title }}
      </el-descriptions-item>
      <el-descriptions-item label="内容">
        {{ model.content }}
      </el-descriptions-item>
      <el-descriptions-item label="发送方">
        <span>{{ model.senderName }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="接收方">
        {{ model.receiverName }}
      </el-descriptions-item>
      <el-descriptions-item label="发送结果">
        <el-tag>{{ model.result }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="定时发送时间">
        {{ model.scheduleTime }}
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
