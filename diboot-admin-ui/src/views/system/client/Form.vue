<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import type { Client } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
import { uuid } from '@/utils/tools'
import PermissionSelect from '@/views/system/resource/components/PermissionSelect.vue'

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

const permissionSelectRef = ref<InstanceType<typeof PermissionSelect>>()

defineExpose({
  init: (id?: string, refresh = true, initData?: Record<string, unknown>) => {
    permissionSelectRef.value?.relocation()
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
  submit: () => {
    if (!model.value.appSecret) model.value.appSecret = uuid()
    return submit(model.value, formRef.value)
  },
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

const appModule = ref<string>()
const moduleList = ref<string[]>([])
</script>

<template>
  <el-form ref="formRef" v-loading="loading" :model="model" label-width="120px">
    <el-row :gutter="18">
      <el-col :span="12">
        <el-form-item
          prop="name"
          :label="$t('client.name')"
          :rules="{ required: true, message: $t('rules.notnull'), whitespace: true }"
        >
          <el-input v-model="model.name" clearable :placeholder="$t('client.namePlaceholder')" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="status" :label="$t('client.status')">
          <el-select v-model="model.status" filterable clearable :placeholder="$t('client.statusPlaceholder')">
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
          <el-input v-model="model.appKey" clearable :placeholder="$t('client.appKeyPlaceholder')" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="appSecret" label="AppSecret">
          <el-input v-model="model.appSecret" clearable disabled :placeholder="$t('client.appSecretPlaceholder')" />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item prop="permissions" :label="$t('client.permissions')">
          <div style="display: flex; width: 100%">
            <el-select
              v-if="moduleList?.length"
              v-model="appModule"
              :placeholder="$t('resource.appModule')"
              clearable
              style="width: 120px !important; margin-right: 3px"
            >
              <el-option v-for="item in moduleList" :key="item" :label="item" :value="item" />
            </el-select>
            <el-select
              v-model="model.permissions"
              multiple
              clearable
              popper-class="hide"
              style="flex: 1"
              :placeholder="$t('client.permissionsPlaceholder')"
            />
          </div>
        </el-form-item>
      </el-col>
    </el-row>
    <permission-select
      ref="permissionSelectRef"
      v-model:permission-codes="model.permissions"
      :app-module="appModule"
      open-api
      menu-type="MENU"
      style="height: calc(100vh - 390px)"
      @module-list="value => (moduleList = value)"
    />
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
