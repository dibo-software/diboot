<script lang="ts" setup>
import { reactive, ref } from 'vue'
import PermissionList from './permissionList/index.vue'
import { Plus } from '@element-plus/icons-vue'

import type { FormInstance, FormRules } from 'element-plus'
import type { ResourcePermission, PermissionGroupType } from './type'
const ruleFormRef = ref<FormInstance>()

const permissionList = reactive<ResourcePermission[]>([])
let permissionCodes = reactive<string[]>([])
const currentPermissionActiveKey = ref(0)
const currentPermissionTitle = ref('菜单页面接口配置')
const originApiList = reactive<PermissionGroupType[]>([])
const currentConfigCode = ref('')
const currentPermissionCodes = reactive<string[]>([])
const showPermission = ref(false)
const rules = reactive<FormRules>({
  displayName: [
    {
      required: true,
      message: '请输入菜单名称',
      trigger: 'blur'
    }
  ]
})
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log(model)
      console.log('submit!')
    } else {
      console.log('error submit!', fields)
    }
  })
}

const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
const handleChangePermissionCodes = (paramPermissionCodes: string[]) => {
  if (currentConfigCode.value === 'Menu') {
    permissionCodes = paramPermissionCodes
  } else {
    permissionList[currentPermissionActiveKey.value].permissionCodes = paramPermissionCodes
  }
}

const props = defineProps<{ formValue: Partial<ResourcePermission> }>()
const model = ref<Partial<ResourcePermission>>({
  routeMeta: {}
})
const empty = ref(true)
watch(
  () => props.formValue,
  val => {
    empty.value = false
    model.value = val
  }
)
</script>
<template>
  <el-empty v-if="empty" description="选择左侧菜单后操作" />
  <el-row v-else :gutter="5">
    <el-col :span="10" class="config-container">
      <el-space wrap :fill="true">
        <el-form ref="ruleFormRef" :model="model" :rules="rules" label-width="90px">
          <div class="card-header">菜单配置</div>
          <el-form-item label="上级菜单" prop="parentId">
            <el-input :modelValue="model.parentId === '0' ? '顶级菜单' : model.parentDisplayName" disabled />
          </el-form-item>
          <el-form-item label="菜单分类" prop="displayType">
            <el-radio-group v-model="model.displayType">
              <el-radio-button label="CATALOGUE">目录</el-radio-button>
              <el-radio-button label="MENU">菜单</el-radio-button>
              <el-radio-button label="OUTSIDE_URL">外链</el-radio-button>
              <el-radio-button label="IFRAME">iframe</el-radio-button>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="菜单名称" prop="displayName">
            <el-input v-model="model.displayName" placeholder="请输入菜单名称" />
          </el-form-item>
          <el-form-item label="菜单编码">
            <el-input v-model="model.resourceCode" placeholder="请输入菜单编码" />
          </el-form-item>
          <el-form-item label="菜单图标">
            <icon-select v-model="model.routeMeta.icon" />
          </el-form-item>
          <el-form-item label="路由地址">
            <el-input v-model="model.routePath" placeholder="请输入路由地址" />
          </el-form-item>
          <el-form-item label="重定向地址">
            <el-input v-model="model.redirectPath" placeholder="请输入重定向地址" />
          </el-form-item>
          <el-form-item label="组件地址">
            <el-input v-model="model.routeMeta.componentPath" placeholder="请输入组件地址" />
          </el-form-item>
          <el-form-item label="高级配置" />
        </el-form>
        <el-card class="box-card" shadow="never">
          <template #header>
            <div class="card-header">
              <span>按钮权限配置</span>
              <el-button class="button" text :icon="Plus" />
            </div>
          </template>
          <div v-for="o in 4" :key="o" class="text item">{{ 'List item ' + o }}</div>
        </el-card>
      </el-space>
    </el-col>
    <el-col :span="14" class="config-container">
      <permission-list
        ref="permissionList"
        :title="currentPermissionTitle"
        :current-permission-codes="currentPermissionCodes"
        :config-code="currentConfigCode"
        :origin-api-list="originApiList"
        @change-permission-codes="handleChangePermissionCodes"
      />
    </el-col>
  </el-row>
  <div v-if="!empty" class="is-fixed" style="text-align: center; margin-top: 10px">
    <el-button type="primary" @click="submitForm(ruleFormRef)">保存</el-button>
    <el-button @click="resetForm(ruleFormRef)">重置</el-button>
  </div>
</template>
<style scoped lang="scss">
.config-container {
  .el-space {
    width: 100%;
  }
}
</style>
