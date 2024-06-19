<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import type { SystemConfig } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/system-config'

const { loadData, loading, model } = useDetail<SystemConfig>(baseApi, { dataType: 'text' })

const title = ref('')

const visible = ref(false)

defineExpose({
  open: (id?: string) => {
    title.value = id ? i18n.t('title.update') : i18n.t('title.create')
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

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = isContinueAdd.value
    if (isContinueAdd.value) formRef.value?.resetFields()
  }
})

// 保存之前判断是否确认并继续添加
const onSubmit = (continueAdd = true) => {
  isContinueAdd.value = continueAdd
  submit(model.value, formRef.value)
}

const checkPropKeyDuplicate = checkValue(
  `${baseApi}/check-prop-key-duplicate`,
  'propKey',
  () => model.value?.id,
  () => ({ category: model.value.category })
)
</script>

<template>
  <el-dialog v-model="visible" :title="title" width="900">
    <el-form ref="formRef" v-loading="loading" :model="model" :label-width="$i18n.locale === 'en' ? '130px' : '80px'">
      <el-row>
        <el-col :span="12">
          <el-form-item prop="category" :label="$t('config.category')">
            <el-input v-model="model.category" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="dataType" :label="$t('config.dataType')">
            <el-radio-group v-model="model.dataType">
              <el-radio-button label="text">{{ $t('config.dataTypeOptions.text') }}</el-radio-button>
              <el-radio-button label="textarea">{{ $t('config.dataTypeOptions.textarea') }}</el-radio-button>
              <el-radio-button label="number">{{ $t('config.dataTypeOptions.number') }}</el-radio-button>
              <el-radio-button label="boolean">{{ $t('config.dataTypeOptions.boolean') }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            prop="propKey"
            :label="$t('config.propKey')"
            :rules="[
              { required: true, message: i18n.t('rules.notnull'), whitespace: true },
              { validator: checkPropKeyDuplicate, trigger: 'blur' }
            ]"
          >
            <el-input v-model="model.propKey" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="propValue" :label="$t('config.propValue')">
            <el-input v-if="model.dataType === 'text'" v-model="model.propValue" />
            <el-input v-if="model.dataType === 'textarea'" v-model="model.propValue" type="textarea" />
            <el-input-number
              v-if="model.dataType === 'number'"
              :model-value="Number(model.propValue)"
              @update:model-value="model.propValue = `${$event}`"
            />
            <el-switch
              v-if="model.dataType === 'boolean'"
              :model-value="JSON.parse(`${model.propValue ?? false}`)"
              @change="model.propValue = `${$event}`"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button v-if="!model.id" type="primary" :loading="submitting" @click="onSubmit(true)">
        {{ $t('button.continueAdd') }}
      </el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit(false)">{{ $t('button.save') }}</el-button>
    </template>
  </el-dialog>
</template>
