<script setup lang="ts" name="UserPositionTableForm">
import type { UserPosition } from './type'
import type { OrgModel } from '../org/type'
import _ from 'lodash'
import type { FormInstance } from 'element-plus'
import positionPopoverListSelector from '@/views/org-structure/position/PopoverListSelector.vue'

type Props = {
  userId?: string
  orgId?: string
  orgTree: OrgModel[]
}
const props = defineProps<Props>()
const dataList = ref<UserPosition[]>([])

const initModel = {
  userId: props.userId || '',
  positionId: '',
  orgId: props.orgId || '',
  isPrimaryPosition: false
}
// 添加数据字典条目
const addItem = () => {
  dataList.value.push(_.cloneDeep(initModel))
}
// 移除数据字典条目
const removeItem = (index: number) => {
  dataList.value.splice(index, 1)
}
// 表单校验
const formRef = ref<FormInstance>()
const validate = () => {
  if (!formRef) return
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
  validate
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
            <position-popover-list-selector v-model="scope.row.positionId" :multi="false" />
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
              :props="{ label: 'shortName', value: 'id' }"
              :default-expand-all="true"
              :check-strictly="true"
            />
          </el-form-item>
        </template>
      </el-table-column>
      <el-table-column label="主岗" width="100">
        <template #default="scope">
          <el-form-item :prop="`${scope.$index}.isPrimaryPosition`">
            <el-switch v-model="scope.row.isPrimaryPosition" />
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
