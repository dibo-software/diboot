<script setup lang="ts">
import type { FormInstance, FormValidateCallback } from 'element-plus'
import type { Group } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
import type { Select } from '@/components/di/type'

const i18n = useI18n()

const baseApi = '/iam/group'

defineProps<{
  // 禁用表单内的所有组件
  disabled?: boolean
  // 禁用属性列表（只读不可输入）
  disabledProps?: string[]
  // 不可见属性列表（忽略不加载）
  invisibleProps?: string[]
}>()

const emit = defineEmits<{
  (e: 'complete', id?: string, isNew?: boolean): void
  (e: 'submitting', submitting: boolean): void
}>()

const { relatedData, initRelatedData } = useOption({
  load: {
    orgIdOptions: { type: 'IamOrg', label: 'name', parent: 'parentId', lazyChild: false }
  }
})

const { loadData, loading, model } = useDetail<Group>(baseApi)

const { submitting, submit } = useForm({ baseApi, successCallback: (id, isNew) => emit('complete', id, isNew) })
watch(submitting, value => emit('submitting', value))

//  表单
const formRef = ref<FormInstance>()

const validate = (
  callback = (valid: boolean) => !valid && ElMessage.error({ message: i18n.t('form.validationFailed'), grouping: true })
) =>
  Promise.all([formRef.value].map(e => e?.validate?.(callback as any as FormValidateCallback)).filter(e => !!e))
    .then((arr: (boolean | undefined)[]) => arr.every(e => e))
    .catch(() => false)

defineExpose({
  init: (id?: string, refresh = true, initData?: Record<string, unknown>) => {
    initRelatedData()
    if (model.value.id === id && !refresh) return
    // 加载数据
    loadData(id).then(() => {
      if (!id) {
        if (initData) Object.keys(initData).forEach(key => (model.value[key as keyof Group] = initData[key] as any))
      }
    })
  },
  validate,
  getData: async (relatedLabel = false) => {
    const data: Record<string, unknown> = _.cloneDeep(model.value)
    if (!relatedLabel) return data
    return data
  },
  submit: () => submit(model.value, formRef.value),
  reset: () => {
    formRef.value?.resetFields()
    model.value.id = void 0
  }
})

const checkNameDuplicate = checkValue(`${baseApi}/check-name-duplicate`, 'name', () => model.value?.id)
</script>

<template>
  <el-form ref="formRef" v-loading="loading" :model="model" label-width="90px" :disabled="disabled">
    <el-row :gutter="18">
      <el-col v-if="!invisibleProps?.includes('name')" :span="24">
        <el-form-item
          prop="name"
          :label="$t('group.name')"
          :rules="[
            { required: true, message: $t('rules.notnull'), whitespace: true },
            { validator: checkNameDuplicate, trigger: 'blur' }
          ]"
        >
          <el-input v-model="model.name" :disabled="disabledProps?.includes('name')" clearable />
        </el-form-item>
      </el-col>
      <el-col v-if="!invisibleProps?.includes('orgId')" :span="24">
        <el-form-item prop="orgId" :label="$t('group.orgId')">
          <el-tree-select
            v-model="model.orgId"
            :data="relatedData.orgIdOptions"
            filterable
            default-expand-all
            check-strictly
            :disabled="disabledProps?.includes('orgId')"
            clearable
          />
        </el-form-item>
      </el-col>
      <el-col v-if="!invisibleProps?.includes('members')" :span="24">
        <el-form-item
          prop="members"
          :label="$t('group.members')"
          :rules="{ required: true, message: $t('rules.notnull') }"
        >
          <di-selector
            v-model="model.members"
            data-type="IamUser"
            data-label="realname"
            :tree="{ type: 'IamOrg', label: 'name', parent: 'parentId', parentPath: 'parentIdsPath' }"
            :list="{
              baseApi: '/iam/user',
              relatedKey: 'orgId',
              searchArea: {
                propList: [
                  { prop: 'realname', label: $t('user.realname'), type: 'input' },
                  { prop: 'userNum', label: $t('user.userNum'), type: 'input' },
                  { prop: 'gender', label: $t('user.gender'), type: 'select', loader: 'GENDER' } as Select
                ]
              },
              columns: [
                { prop: 'userNum', label: $t('user.userNum') },
                { prop: 'realname', label: $t('user.realname') },
                { prop: 'genderLabel', label: $t('user.gender') },
                { prop: 'mobilePhone', label: $t('user.mobilePhone') },
                { prop: 'sortId', label: $t('user.sortId') }
              ]
            }"
            multiple
          />
        </el-form-item>
      </el-col>
      <el-col v-if="!invisibleProps?.includes('description')" :span="24">
        <el-form-item prop="description" :label="$t('group.description')">
          <el-input
            v-model="model.description"
            type="textarea"
            autosize
            :disabled="disabledProps?.includes('description')"
            clearable
          />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>

<style scoped lang="scss"></style>
