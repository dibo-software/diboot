<script setup lang="ts" name="UserForm">
import type { FormInstance, FormRules } from 'element-plus'
import type { UserModel } from './type'
import { defineEmits } from 'vue'
import useTree from '@/hooks/tree'
import type { OrgModel } from '@/views/orgUser/org/type'
import PopoverListSelector from './popoverListSelector.vue'
import type UserPositionTableForm from '../position/userPositionTableForm.vue'

const baseApi = '/user'

const { loadData, loading, model } = useDetailDefault<UserModel>(baseApi)
const {
  getTree,
  treeDataList: orgTree,
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
    // 判定是否属于系统用户
    if (!id) {
      model.value.isSysAccount = false
    } else {
      model.value.isSysAccount = !!model.value.username
    }
    // 加载树结构数据
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

const userPositionTableForm = ref<InstanceType<typeof UserPositionTableForm>>()
const { confirmSubmit, submit } = useFormDefault({
  baseApi,
  async afterValidate() {
    await userPositionTableForm.value?.validate()
  },
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const rules: FormRules = {
  orgId: { required: true, message: '不能为空', whitespace: true },
  username: { required: true, message: '不能为空', whitespace: true },
  password: { required: true, message: '不能为空', whitespace: true },
  realname: { required: true, message: '不能为空', whitespace: true },
  userNum: { required: true, message: '不能为空', whitespace: true },
  gender: { required: true, message: '不能为空', whitespace: true },
  status: { required: true, message: '不能为空', whitespace: true }
}
</script>

<template>
  <el-dialog v-model="visible" :title="title" :width="720">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item :required="true" prop="isSysAccount" label="用户类型">
            <el-radio-group v-model="model.isSysAccount">
              <el-radio-button :label="false">普通用户</el-radio-button>
              <el-radio-button :label="true">系统用户</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="orgId" label="所属部门">
            <el-tree-select
              v-model="model.orgId"
              placeholder="请选择部门"
              class="tree-selector"
              :data="orgTree"
              :props="{ label: 'shortName', value: 'id' }"
              :default-expand-all="true"
              :check-strictly="true"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="model.isSysAccount" :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="model.username" placeholder="请输入用户名" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="password" label="密码">
            <el-input v-model="model.password" placeholder="请输入密码" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="realname" label="姓名">
            <el-input v-model="model.realname" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="userNum" label="编号">
            <el-input v-model="model.userNum" placeholder="请输入编号" />
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
            <el-input v-model="model.birthday" placeholder="请输入编号" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="mobilePhone" label="电话">
            <el-input v-model="model.mobilePhone" placeholder="请输入电话" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="model.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="roles" label="角色">
            <popover-list-selector v-model="model.roleIds" :multi="true" />
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
      <user-position-table-form
        ref="userPositionTableForm"
        :org-tree="orgTree"
        :user-id="model.id"
        :org-id="model.orgId"
      />
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
