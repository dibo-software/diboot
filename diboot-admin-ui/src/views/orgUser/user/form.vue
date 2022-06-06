<script setup lang="ts" name="UserForm">
import { FormInstance, FormRules } from 'element-plus'
import type { UserModel } from './type'
import { defineEmits } from 'vue'
import useTree from '@/views/system/resourcePermission/hooks/tree'
import { OrgModel } from '@/views/orgUser/org/type'

const baseApi = '/user'

const { loadData, loading, model } = useDetailDefault<UserModel>(baseApi)
const {
  getTree,
  treeDataList,
  loading: treeLoading
} = useTree<OrgModel>({
  baseApi: '/org',
  treeApi: '/tree',
  transformField: { label: 'shortName' }
})

const title = ref('')

const visible = ref(false)

defineExpose({
  open: async (id?: string) => {
    title.value = id ? '更新用户信息' : '新建用户'
    visible.value = true
    await loadData(id)
    await getTree()
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

const { confirmSubmit, submit } = useFormDefault({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const rules: FormRules = {
  realname: { required: true, message: '不能为空', whitespace: true },
  userNum: { required: true, message: '不能为空', whitespace: true },
  gender: { required: true, message: '不能为空', whitespace: true },
  status: { required: true, message: '不能为空', whitespace: true }
}
</script>

<template>
  <el-dialog v-model="visible" :title="title">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-form-item prop="orgId" label="所属部门">
        <el-tree-select
          v-model="model.orgId"
          class="tree-selector"
          :data="treeDataList"
          :props="{ label: 'shortName', value: 'id' }"
          :default-expand-all="true"
          :check-strictly="true"
        />
      </el-form-item>
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="realname" label="姓名">
            <el-input v-model="model.realname" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="userNum" label="编号">
            <el-input v-model="model.userNum" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="gender" label="性别">
            <el-select v-model="model.gender">
              <el-option key="M" label="男" value="M" />
              <el-option key="F" label="女" value="F" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="birthday" label="生日">
            <el-input v-model="model.birthday" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="mobilePhone" label="电话">
            <el-input v-model="model.mobilePhone" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="model.email" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="status" label="状态">
            <el-select v-model="model.status">
              <el-option key="A" label="正常" value="A" />
              <el-option key="S" label="停用" value="S" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="confirmSubmit" @click="submit(formRef, model)">提交</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.variable-tag {
  margin: 0 5px 5px 0;
  cursor: pointer;
}
</style>
