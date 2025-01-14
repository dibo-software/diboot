<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import type { Client } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'

const i18n = useI18n()

const baseApi = '/client'

const emit = defineEmits<{
  (e: 'complete', id?: string, isNew?: boolean): void
  (e: 'submitting', submitting: boolean): void
}>()

const { initRelatedData, relatedData } = useOption({ dict: ['ACCOUNT_STATUS'] })

const { loadData, loading, model } = useDetail<Client>(baseApi, { status: 'A' })

const { submitting, submit } = useForm({ baseApi, successCallback: (id, isNew) => emit('complete', id, isNew) })
watch(submitting, value => emit('submitting', value))

//  表单
const formRef = ref<FormInstance>()

const validate = (
  callback = (valid: boolean) => {
    !valid && ElMessage.error({ message: i18n.t('form.validationFailed'), grouping: true })
  }
) =>
  Promise.all([formRef.value].map(e => e?.validate?.(callback)).filter(e => !!e))
    .then((arr: (boolean | undefined)[]) => arr.every(e => e))
    .catch(() => false)

defineExpose({
  init: (id?: string, refresh = true, initData?: Record<string, unknown>) => {
    // 初始化选项
    initRelatedData()
    if (model.value.id === id && !refresh) return
    // 加载数据
    loadData(id).then(() => {
      if (!id) {
        if (initData) Object.keys(initData).forEach(key => (model.value[key as keyof Client] = initData[key] as any))
      }
    })
  },
  validate,
  getData: async (relatedLabel = false) => {
    const data: Record<string, unknown> = _.cloneDeep(model.value)
    if (!relatedLabel) return data

    data.statusLabel = relatedData.accountStatusOptions.find(e => e.value === data.status)

    return data
  },
  submit: () => submit(model.value, formRef.value),
  reset: () => {
    formRef.value?.resetFields()
    model.value.id = void 0
  }
})

// AppKey 检查值是否重复
const checkAppKeyDuplicate = checkValue(
  `${baseApi}/check-unique`,
  'value',
  () => model.value?.id,
  () => ({ field: 'appKey' })
)
</script>

<template>
  <el-form ref="formRef" v-loading="loading" :model="model" label-width="90px">
    <el-row :gutter="18">
      <el-col :span="12">
        <el-form-item
          prop="name"
          :label="$t('client.name')"
          :rules="{ required: true, message: $t('rules.notnull'), whitespace: true }"
        >
          <el-input v-model="model.name" clearable />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="status" :label="$t('client.status')">
          <el-select v-model="model.status" filterable clearable>
            <el-option v-for="item in relatedData.accountStatusOptions" :key="item.value" v-bind="item" />
          </el-select>
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item
          prop="appKey"
          label="AppKey"
          :rules="[
            { required: true, message: $t('rules.notnull'), whitespace: true },
            { validator: checkAppKeyDuplicate, trigger: 'blur' }
          ]"
        >
          <el-input v-model="model.appKey" clearable />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item
          prop="appSecret"
          label="AppSecret"
          :rules="{ required: true, message: $t('rules.notnull'), whitespace: true }"
        >
          <el-input v-model="model.appSecret" clearable />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item prop="permissions" :label="$t('client.permissions')">
          <el-select
            v-model="model.permissions"
            :no-data-text="$t('client.permissionsCreate')"
            multiple
            filterable
            allow-create
            default-first-option
            clearable
          />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<style scoped lang="scss">
.option {
  display: flex;
  justify-content: space-between;

  .ext {
    font-size: var(--el-font-size-extra-small);
    color: var(--el-text-color-secondary);
  }
}
</style>
