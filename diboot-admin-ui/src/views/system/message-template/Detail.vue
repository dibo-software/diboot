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
  <el-dialog v-model="visible" title="消息模板详情" width="65vw">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item label="业务模块">
        {{ model.appModule }}
      </el-descriptions-item>
      <el-descriptions-item label="标题">
        {{ model.title }}
      </el-descriptions-item>
      <el-descriptions-item label="内容">
        {{ model.content }}
      </el-descriptions-item>
      <el-descriptions-item label="创建人">
        <span>{{ model.createByName }}</span>
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
