<script setup lang="ts">
import type { FormInstance, FormValidateCallback } from 'element-plus'
import type { Resource } from '../type'
import { tree2List } from '@/utils/list'
import { useI18n } from 'vue-i18n'

const i18n = useI18n()

const baseApi = '/iam/resource'

const emit = defineEmits<{
  (e: 'complete', id?: string, isNew?: boolean): void
  (e: 'submitting', submitting: boolean): void
}>()

const { initRelatedData, relatedData } = useOption({
  load: {
    parentIdOptions: {
      type: 'IamResource',
      label: 'displayName',
      parent: 'parentId',
      lazyChild: false,
      conditions: [{ field: 'appModule', value: 'mobile' }]
    }
  }
})

const { loadData, loading, model } = useDetail<Resource>(baseApi, { appModule: 'mobile', displayType: 'PERMISSION' })

const { submitting, submit } = useForm({ baseApi, successCallback: (id, isNew) => emit('complete', id, isNew) })
watch(submitting, value => emit('submitting', value))

//  表单
const formRef = ref<FormInstance>()

const validate = (
  callback: FormValidateCallback = (valid: boolean) => {
    if (!valid) ElMessage.error({ message: i18n.t('form.validationFailed'), grouping: true })
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
        if (initData) Object.keys(initData).forEach(key => (model.value[key as keyof Resource] = initData[key] as any))
      }
    })
  },
  validate,
  getData: async (relatedLabel = false) => {
    const data: Record<string, unknown> = _.cloneDeep(model.value)
    if (!relatedLabel) return data
    if ((data.parentId as string)?.length) {
      let options = relatedData.parentIdOptions
      options = tree2List(options)
      data.parentIdLabel = options.find(e => e.value === data.parentId)?.label
    } else data.parentIdLabel = undefined

    return data
  },
  submit: () => submit(model.value, formRef.value),
  reset: () => {
    formRef.value?.resetFields()
    model.value.id = void 0
  }
})
</script>

<template>
  <el-form ref="formRef" v-loading="loading" :model="model" label-width="90px">
    <el-form-item prop="parentId" :label="$t('resource.mobile.parentId')">
      <el-tree-select
        v-model="model.parentId"
        :data="relatedData.parentIdOptions"
        filterable
        default-expand-all
        check-strictly
        clearable
        :placeholder="$t('resource.mobile.parentPlaceholder')"
      />
    </el-form-item>
    <el-form-item
      prop="displayName"
      :label="$t('resource.mobile.displayName')"
      :rules="{ required: true, message: $t('rules.notnull'), whitespace: true }"
    >
      <el-input v-model="model.displayName" clearable :placeholder="$t('resource.mobile.displayNamePlaceholder')" />
    </el-form-item>
    <el-form-item
      prop="resourceCode"
      :label="$t('resource.mobile.resourceCode')"
      :rules="[{ required: true, message: $t('rules.notnull'), whitespace: true }]"
    >
      <el-input v-model="model.resourceCode" clearable :placeholder="$t('resource.mobile.resourceCodePlaceholder')" />
    </el-form-item>
  </el-form>
</template>

<style scoped lang="scss"></style>
