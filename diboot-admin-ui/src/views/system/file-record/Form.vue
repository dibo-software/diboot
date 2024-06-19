<script setup lang="ts">
import type { FormInstance } from 'element-plus'

const baseApi = '/file-record'

const { loadData, loading, model } = useDetail<FileRecord>(baseApi)

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
// 表单
const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) formRef.value?.resetFields()
})

const emit = defineEmits<{
  (e: 'complete', id?: string): void
}>()

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})
</script>

<template>
  <el-dialog v-model="visible" :title="$t('fileRecord.editDescription')">
    <el-form ref="formRef" v-loading="loading" :model="model" label-width="80px">
      <el-form-item :label="$t('fileRecord.description')">
        <el-input
          v-model="model.description"
          type="textarea"
          :autosize="{ minRows: 3 }"
          :placeholder="$t('fileRecord.placeholder.description')"
          :maxlength="100"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="submit(model, formRef)">{{
        $t('button.save')
      }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss"></style>
