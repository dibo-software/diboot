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
  <el-dialog v-model="visible" :title="$t('title.detail')" width="65vw">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item :label="$t('message.businessType')">
        {{ model.businessType }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.businessCode')">
        <span>{{ model.businessCode }}</span>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.title')">
        {{ model.title }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.content')">
        {{ model.content }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.senderName')">
        <span>{{ model.senderName }}</span>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.receiverName')">
        {{ model.receiverName }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.channel')">
        <el-tag :color="model.channelLabel?.ext?.color" effect="dark" type="info">
          {{ model.channelLabel?.label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.result')">
        <el-tag>{{ model.result }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.status')">
        <el-tag :color="model.statusLabel?.ext?.color" effect="dark" type="info">
          {{ model.statusLabel?.label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.scheduleTime')">
        {{ model.scheduleTime }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('message.createTime')">
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
