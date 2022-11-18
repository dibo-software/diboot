<script setup lang="ts">
import { fileDownload } from '@/utils/file'

const { model, loadData, loading } = useDetail<FileRecord>('/file-record')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
</script>

<template>
  <el-dialog v-model="visible" title="文件详情" width="65vw">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item v-if="model.appModule" label="业务模块">
        {{ model.appModule }}
      </el-descriptions-item>
      <el-descriptions-item label="文件名称">
        {{ model.fileName }}
      </el-descriptions-item>
      <el-descriptions-item label="文件大小"> {{ model.fileSizeLabel }} </el-descriptions-item>
      <el-descriptions-item label="访问地址">
        <el-link type="primary" @click="model.accessUrl && fileDownload(model.accessUrl)">
          {{ model.accessUrl }}
        </el-link>
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">
        {{ model.description }}
      </el-descriptions-item>
      <el-descriptions-item label="创建人">
        {{ model.createByName }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ model.createTime }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
