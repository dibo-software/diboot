<script lang="ts" setup>
import { reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { ResourcePermission } from './type'
const formSize = ref('default')
const ruleFormRef = ref<FormInstance>()

const model = reactive<ResourcePermission>({
  parentId: 0,
  displayType: '',
  displayName: '',
  routePath: '',
  redirectPath: '',
  resourceCode: '',
  permissionCode: [],
  metaConfig: {}
})

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
</script>
<template>
  <el-row :gutter="10">
    <el-col :span="10" class="config-container">
      <el-card class="box-card" shadow="never">
        <el-form
          ref="ruleFormRef"
          :model="model"
          :rules="rules"
          label-width="120px"
          class="demo-ruleForm"
          :size="formSize"
        >
          <div class="card-header">
            <span>菜单配置</span>
            <el-button class="button" text icon="plus"></el-button>
          </div>
          <el-form-item label="上级菜单" prop="parentId">
            <el-input v-model="model.parentId" disabled />
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
            <el-input v-model="model.displayName" />
          </el-form-item>
          <el-form-item label="菜单编码">
            <el-input v-model="model.resourceCode" />
          </el-form-item>
          <el-form-item label="菜单图标">
            <el-input v-model="model.metaConfig.icon" />
          </el-form-item>
          <el-form-item label="路由地址">
            <el-input v-model="model.routePath" />
          </el-form-item>
          <el-form-item label="重定向地址">
            <el-input v-model="model.redirectPath" />
          </el-form-item>
          <el-form-item label="组件地址">
            <el-input v-model="model.metaConfig.componentPath" />
          </el-form-item>
          <el-form-item label="高级配置"> </el-form-item>
        </el-form>
      </el-card>
      <el-card class="box-card" style="margin-top: 20px" shadow="never">
        <template #header>
          <div class="card-header">
            <span>按钮权限配置</span>
            <el-button class="button" text icon="plus"></el-button>
          </div>
        </template>
        <div v-for="o in 4" :key="o" class="text item">{{ 'List item ' + o }}</div>
      </el-card>
    </el-col>
    <el-col :span="10" class="config-container"> </el-col>
  </el-row>

  <div style="text-align: center; margin-top: 10px">
    <el-button type="primary" @click="submitForm(ruleFormRef)">保存</el-button>
    <el-button @click="resetForm(ruleFormRef)">重置</el-button>
  </div>
</template>
<style scoped></style>
