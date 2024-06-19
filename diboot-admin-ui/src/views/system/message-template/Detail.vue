<script setup lang="ts">
import type { MessageTemplate } from './type'

const { model, loadData, loading } = useDetail<MessageTemplate>('/message-template')

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
      <el-descriptions-item :label="$t('messageTemplate.appModule')">
        {{ model.appModule }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('messageTemplate.title')">
        {{ model.title }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('messageTemplate.content')">
        {{ model.content }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createBy')">
        <span>{{ model.createByName }}</span>
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
