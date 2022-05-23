<script setup lang="ts" name="RoleForm">
import { FormInstance, FormRules } from 'element-plus'
import type { Role } from './type'
import { defineEmits } from 'vue'

const baseApi = '/role'

const { loadData, loading, model } = useDetailDefault<Role>(baseApi)

const title = ref('')

const visible = ref(false)

defineExpose({
  open: (id?: string) => {
    title.value = id ? '更新' : '新建'
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

const { confirmSubmit, submit } = useFormDefault({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const rules: FormRules = {
  name: { required: true, message: '不能为空', whitespace: true },
  code: { required: true, message: '不能为空', whitespace: true }
}
</script>

<template>
  <el-dialog v-model="visible" :title="title">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-form-item prop="name" label="名称">
        <el-input v-model="model.name" />
      </el-form-item>
      <el-form-item prop="code" label="编码">
        <el-input v-model="model.code" />
      </el-form-item>
      <el-form-item prop="description" label="备注">
        <el-input v-model="model.description" type="textarea" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="confirmSubmit" @click="submit(formRef, model)">提交</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
