<script setup lang="ts">
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
  <el-dialog v-model="visible" :title="$t('title.detail')" width="65vw">
    <el-descriptions v-loading="loading" :column="2" class="margin-top" border>
      <el-descriptions-item v-if="model.appModule" :label="$t('fileRecord.appModule')">
        {{ model.appModule }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('fileRecord.fileName')">
        {{ model.fileName }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('fileRecord.fileSize')"> {{ model.fileSizeLabel }} </el-descriptions-item>
      <el-descriptions-item :label="$t('fileRecord.accessUrl')">
        <download
          type="primary"
          link
          :url="model.accessUrl ?? ''"
          style="white-space: break-spaces; overflow-wrap: anywhere; user-select: auto"
        >
          {{ model.accessUrl }}
        </download>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('fileRecord.description')" :span="2">
        {{ model.description }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createBy')">
        {{ model.createByName }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createTime')">
        {{ model.createTime }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.close') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
