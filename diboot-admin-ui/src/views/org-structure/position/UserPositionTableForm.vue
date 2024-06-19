<script setup lang="ts">
import type { UserPosition } from './type'
import _ from 'lodash'
import type { FormInstance } from 'element-plus'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()

type Props = {
  userId?: string
  orgId?: string
  orgTree?: LabelValue[]
  modelValue?: UserPosition[]
}
const props = defineProps<Props>()
const dataList = ref<UserPosition[]>([])

watch(
  () => props.modelValue,
  value => {
    if (value && value.length) dataList.value = value
    else dataList.value.length = 0
  },
  { immediate: true }
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: UserPosition[]): void
}>()

watch(dataList, value => emit('update:modelValue', value), { deep: true })

const initModel = {
  userId: props.userId || '',
  positionId: '',
  orgId: props.orgId || '',
  isPrimaryPosition: false
}

const isPrimaryPositionChange = (index: number) => {
  dataList.value.forEach((row, i) => {
    row.isPrimaryPosition = index === i
  })
}
// 添加数据字典条目
const addItem = () => {
  dataList.value.push(_.cloneDeep(initModel))
  // 第一条数据默认选中主岗
  dataList.value.length === 1 && (dataList.value[0].isPrimaryPosition = true)
}
// 移除数据字典条目
const removeItem = (index: number) => {
  dataList.value.splice(index, 1)
  // 当删除的是选中主岗的数据，则让数组第一条数据选中主岗
  if (dataList.value.length !== 0 && !dataList.value.some(v => v.isPrimaryPosition)) {
    dataList.value[0].isPrimaryPosition = true
  }
}
// 清空数据字典
const clearDataList = () => {
  dataList.value.splice(0)
}
// 表单校验
const formRef = ref<FormInstance>()
const validate = () => {
  if (!formRef.value) return
  return new Promise((resolve, reject) => {
    formRef.value?.validate((valid, fields) => {
      if (valid) {
        resolve(fields)
      } else {
        reject(i18n.t('rules.nonpass'))
      }
    })
  })
}
defineExpose({
  validate,
  clearDataList
})
</script>
<template>
  <el-form ref="formRef" :model="dataList" label-position="top">
    <el-table class="el-table" :data="dataList" style="width: 100%">
      <el-table-column width="250">
        <template #header>
          <span class="required-flag">*</span>
          {{ $t('position.label') }}
        </template>
        <template #default="scope">
          <el-form-item
            :prop="`${scope.$index}.positionId`"
            :rules="{
              required: true,
              message: `${$t('placeholder.select')} ${$t('position.label')}`,
              trigger: 'blur'
            }"
          >
            <di-selector
              v-model="scope.row.positionId"
              :list="{
                baseApi: '/iam/position',
                searchArea: {
                  propList: [
                    { prop: 'name', label: $t('position.name'), type: 'input' },
                    { prop: 'code', label: $t('position.code'), type: 'input' }
                  ]
                },
                columns: [
                  { prop: 'name', label: $t('position.name') },
                  { prop: 'code', label: $t('position.code') },
                  { prop: 'gradeValue', label: $t('position.gradeValue') },
                  { prop: 'gradeName', label: $t('position.gradeNameAlias') },
                  { prop: 'createTime', label: $t('baseField.createTime') }
                ]
              }"
              data-type="IamPosition"
              :placeholder="$t('position.placeholder.label')"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column width="250">
        <template #header>
          <span class="required-flag">*</span>
          {{ $t('org.label') }}
        </template>
        <template #default="scope">
          <el-form-item
            :prop="`${scope.$index}.orgId`"
            :rules="{
              required: true,
              message: `${$t('placeholder.select')} ${$t('org.dept')}`,
              trigger: 'blur'
            }"
          >
            <el-tree-select
              v-model="scope.row.orgId"
              :placeholder="`${$t('placeholder.select')} ${$t('org.dept')}`"
              class="tree-selector"
              :data="orgTree"
              :default-expand-all="true"
              :check-strictly="true"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column :label="$t('org.primaryPosition')" width="100">
        <template #default="scope">
          <el-form-item :prop="`${scope.$index}.isPrimaryPosition`">
            <el-switch v-model="scope.row.isPrimaryPosition" @change="isPrimaryPositionChange(scope.$index)" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column>
        <template #header>
          <el-button size="small" type="primary" @click="addItem">{{ $t('operation.add') }}</el-button>
        </template>
        <template #default="scope">
          <el-button size="small" type="danger" @click="removeItem(scope.$index)">{{
            $t('operation.delete')
          }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-form>
</template>
<style lang="scss" scoped>
.el-table {
  .required-flag {
    color: var(--el-color-danger);
    font-weight: 400;
  }

  :deep(.el-form-item) {
    margin-bottom: 0;

    &.is-error {
      margin-bottom: 18px;
    }
  }
}
</style>
