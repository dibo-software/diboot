<script setup lang="ts" name="MessageTemplateForm">
import type { FormInstance } from 'element-plus'
import { defineEmits } from 'vue'

const baseApi = '/fileRecord'

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
  primaryKey: 'uuid',
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})
</script>

<template>
  <el-dialog v-model="visible" title="编辑备注">
    <el-form ref="formRef" v-loading="loading" :model="model" label-width="80px">
      <el-form-item label="备注">
        <el-input
          v-model="model.description"
          type="textarea"
          :autosize="{ minRows: 3 }"
          placeholder="请输入备注"
          :maxlength="100"
          show-word-limit
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit(model, formRef)">提交</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss"></style>
