<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { Position } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/iam/position'

const { loadData, loading, model } = useDetail<Position>(baseApi)
const { relatedData, initRelatedData } = useOption({ dict: ['DATA_PERMISSION_TYPE', 'POSITION_GRADE'] })
const title = ref('')
const visible = ref(false)
defineExpose({
  open: (id?: string) => {
    title.value = id ? i18n.t('title.update') : i18n.t('title.create')
    loadData(id)
    initRelatedData()
    visible.value = true
  }
})

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

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

const onGradeValueChanged = (val: string) => {
  const grade = relatedData.positionGradeOptions.find((item: LabelValue) => item.value === val)
  if (grade !== undefined) {
    model.value.gradeName = grade.label
  }
}

const checkCodeDuplicate = checkValue(`${baseApi}/check-code-duplicate`, 'code', () => model.value?.id)

const rules: FormRules = {
  name: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  code: [
    { required: true, message: i18n.t('rules.notnull'), whitespace: true },
    { validator: checkCodeDuplicate, trigger: 'blur' }
  ]
}
</script>

<template>
  <el-dialog v-model="visible" :width="520" :title="title">
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '120px' : '80px'"
    >
      <el-form-item prop="name" :label="$t('position.name')">
        <el-input v-model="model.name" :placeholder="$t('position.placeholder.name')" />
      </el-form-item>
      <el-form-item prop="code" :label="$t('position.code')">
        <el-input v-model="model.code" :placeholder="$t('position.placeholder.code')" />
      </el-form-item>
      <el-form-item prop="gradeValue" :label="$t('position.gradeName')">
        <el-select
          v-model="model.gradeValue"
          :placeholder="$t('position.placeholder.gradeName')"
          @change="onGradeValueChanged"
        >
          <el-option
            v-for="item in relatedData.positionGradeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="dataPermissionType" :label="$t('position.dataPermissionType')">
        <el-select v-model="model.dataPermissionType" :placeholder="$t('position.placeholder.dataPermissionType')">
          <el-option
            v-for="item in relatedData.dataPermissionTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item prop="isVirtual" :label="$t('position.isVirtual')">
        <el-switch v-model="model.isVirtual" />
      </el-form-item>
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
