<script setup lang="ts">
import type { UserPosition } from './type'
import _ from 'lodash'
import type { FormInstance } from 'element-plus'

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
        reject('校验不通过')
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
          岗位
        </template>
        <template #default="scope">
          <el-form-item
            :prop="`${scope.$index}.positionId`"
            :rules="{
              required: true,
              message: '请选择岗位',
              trigger: 'blur'
            }"
          >
            <di-selector
              v-model="scope.row.positionId"
              :list="{
                baseApi: '/iam/position',
                searchArea: {
                  propList: [
                    { prop: 'name', label: '名称', type: 'input' },
                    { prop: 'code', label: '编码', type: 'input' }
                  ]
                },
                columns: [
                  { prop: 'name', label: '姓名' },
                  { prop: 'code', label: '编号' },
                  { prop: 'gradeValue', label: '职级' },
                  { prop: 'gradeName', label: '职级头衔' },
                  { prop: 'createTime', label: '创建时间' }
                ]
              }"
              data-type="IamPosition"
              placeholder="选择岗位"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column width="250">
        <template #header>
          <span class="required-flag">*</span>
          组织部门
        </template>
        <template #default="scope">
          <el-form-item
            :prop="`${scope.$index}.orgId`"
            :rules="{
              required: true,
              message: '请选择部门',
              trigger: 'blur'
            }"
          >
            <el-tree-select
              v-model="scope.row.orgId"
              placeholder="请选择部门"
              class="tree-selector"
              :data="orgTree"
              :default-expand-all="true"
              :check-strictly="true"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="主岗" width="100">
        <template #default="scope">
          <el-form-item :prop="`${scope.$index}.isPrimaryPosition`">
            <el-switch v-model="scope.row.isPrimaryPosition" @change="isPrimaryPositionChange(scope.$index)" />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column>
        <template #header>
          <el-button size="small" type="primary" @click="addItem">添加</el-button>
        </template>
        <template #default="scope">
          <el-button size="small" type="danger" @click="removeItem(scope.$index)">删除</el-button>
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
