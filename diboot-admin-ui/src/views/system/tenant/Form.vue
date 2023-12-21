<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { Tenant } from './type'
import { checkValue } from '@diboot/common/utils/validate-form'

const baseApi = '/iam/tenant'

const { loadData, loading, model } = useDetail<Tenant>(baseApi, {
  status: 'A'
})
const { relatedData, initRelatedData } = useOption({
  dict: ['TENANT_STATUS']
})
const title = ref('')
const visible = ref(false)
defineExpose({
  open: (id?: string) => {
    title.value = id ? '更新' : '新建'
    loadData(id)
    initRelatedData()
    visible.value = true
  }
})

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

//  表单
const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) {
    formRef.value?.resetFields()
  }
})

const emit = defineEmits<{
  (e: 'complete', id?: string): void
}>()

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = isContinueAdd.value
    if (isContinueAdd.value) {
      formRef.value?.resetFields()
    }
  }
})

// 保存之前判断是否确认并继续添加
const beforeSubmit = (value: boolean) => {
  isContinueAdd.value = value
  submit(model.value, formRef.value)
}
const checkCodeDuplicate = checkValue(`${baseApi}/check-code-duplicate`, 'code', () => model.value?.id)

const rule = { required: true, message: '不能为空', whitespace: true }
const rules: FormRules = {
  name: [rule],
  shortName: [rule],
  code: [rule, { validator: checkCodeDuplicate, trigger: 'blur' }],
  startDate: [rule],
  endDate: [rule],
  manager: [rule],
  phone: [rule],
  status: [rule]
}
</script>

<template>
  <el-dialog v-model="visible" width="60%" :title="title">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="120px">
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="name" label="租户名称">
            <el-input v-model="model.name" placeholder="请输入 租户名称" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="code" label="租户编码">
            <el-input v-model="model.code" placeholder="请输入 租户编码" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="startDate" label="有效开始日期">
            <el-date-picker
              v-model="model.startDate"
              clearable
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择 有效开始日期"
            />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="endDate" label="有效结束日期">
            <el-date-picker
              v-model="model.endDate"
              clearable
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择 有效结束日期"
            />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="manager" label="负责人">
            <el-input v-model="model.manager" placeholder="请输入 负责人" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="phone" label="联系电话">
            <el-input v-model="model.phone" placeholder="请输入 联系电话" />
          </el-form-item>
        </el-col>
        <el-col :md="24" :sm="24">
          <el-form-item prop="description" label="描述">
            <el-input v-model="model.description" type="textarea" placeholder="请输入 描述" />
          </el-form-item>
        </el-col>
        <el-col :md="24" :sm="24">
          <el-form-item prop="status" label="租户状态">
            <el-select v-model="model.status" filterable placeholder="请选择 租户状态" clearable>
              <el-option
                v-for="item in relatedData.tenantStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button v-if="!model.id" type="primary" :loading="submitting" @click="beforeSubmit(true)"
        >保存并继续
      </el-button>
      <el-button type="primary" :loading="submitting" @click="beforeSubmit(false)">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
