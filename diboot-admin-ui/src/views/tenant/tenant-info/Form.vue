<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { Tenant } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/iam/tenant'

const { loadData, loading, model } = useDetail<Tenant & { validDate: [string, string] }>(baseApi, { status: 'A' })

const { relatedData, initRelatedData } = useOption({
  dict: ['TENANT_STATUS']
})
const title = ref('')
const visible = ref(false)
defineExpose({
  open: (id?: string) => {
    title.value = id ? i18n.t('title.update') : i18n.t('title.create')
    loadData(id).then(() => {
      if (model.value.startDate && model.value.endDate)
        model.value.validDate = [model.value.startDate, model.value.endDate]
    })
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
  if (model.value.validDate && model.value.validDate.length > 0) {
    model.value.startDate = model.value.validDate[0]
    model.value.endDate = model.value.validDate[1]
  }
  submit(model.value, formRef.value)
}
const checkCodeDuplicate = checkValue(`${baseApi}/check-code-duplicate`, 'code', () => model.value?.id)

const rule = { required: true, message: i18n.t('rules.notnull'), whitespace: true }
const rules: FormRules = {
  name: [rule],
  code: [rule, { validator: checkCodeDuplicate, trigger: 'blur' }],
  validDate: [{ required: true, message: i18n.t('rules.notnull'), whitespace: true, type: 'array' }],
  status: [rule]
}
</script>

<template>
  <el-dialog v-model="visible" width="60%" :title="title">
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '120px' : '80px'"
    >
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="name" :label="$t('tenantInfo.name')">
            <el-input v-model="model.name" :placeholder="$t('tenantInfo.placeholder.name')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="code" :label="$t('tenantInfo.code')">
            <el-input v-model="model.code" :placeholder="$t('tenantInfo.placeholder.code')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="status" :label="$t('tenantInfo.status')">
            <el-select v-model="model.status" filterable :placeholder="$t('tenantInfo.placeholder.status')" clearable>
              <el-option
                v-for="item in relatedData.tenantStatusOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="validDate" :label="$t('tenantInfo.validDate')">
            <el-date-picker
              v-model="model.validDate"
              type="daterange"
              format="YYYY-MM-DD"
              value-format="YYYY-MM-DD"
              unlink-panels
              :start-placeholder="$t('tenantInfo.placeholder.startDate')"
              :end-placeholder="$t('tenantInfo.placeholder.endDate')"
            />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="manager" :label="$t('tenantInfo.manager')">
            <el-input v-model="model.manager" :placeholder="$t('tenantInfo.placeholder.manager')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item
            prop="phone"
            :label="$t('tenantInfo.phone')"
            :rules="[
              {
                pattern: /^1[3-9]\d{9}$|^0\d{2,3}-\d{7,8}$|^(\+[1-9]{1}[0-9]{3,14})?([0-9]{9,14})$/,
                message: $t('tenantInfo.rules.phone'),
                trigger: ['blur', 'change']
              }
            ]"
          >
            <el-input v-model="model.phone" :placeholder="$t('tenantInfo.placeholder.phone')" />
          </el-form-item>
        </el-col>
        <el-col :md="24" :sm="24">
          <el-form-item prop="description" :label="$t('tenantInfo.description')">
            <el-input
              v-model="model.description"
              type="textarea"
              :placeholder="$t('tenantInfo.placeholder.description')"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button v-if="!model.id" type="primary" :loading="submitting" @click="beforeSubmit(true)"
        >{{ $t('button.continueAdd') }}
      </el-button>
      <el-button type="primary" :loading="submitting" @click="beforeSubmit(false)">{{ $t('button.save') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
