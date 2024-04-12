<script setup lang="ts">
import type { UserModel } from '@/views/org-structure/user/type'
import { checkValue } from '@/utils/validate-form'
import type { FormRules } from 'element-plus'

const baseApi = '/iam/tenant/admin'

const { loadData, loading, model } = useDetail<UserModel>(baseApi)
const { initRelatedData, relatedData } = useOption({
  dict: ['GENDER', 'ACCOUNT_STATUS']
})
initRelatedData()

const tenantId = ref()
const checkUsernameDuplicate = checkValue(
  `${baseApi}/check-username-duplicate`,
  'username',
  () => model.value?.id,
  () => {
    return {
      tenantId: tenantId.value
    }
  }
)

const submitting = ref(false)
const setOrgId = async () => {
  const res = await api.get(`/iam/tenant/org/${tenantId.value}`)
  if (res.code === 0) {
    model.value.userNum = '000'
    model.value.status = 'A'
    model.value.gender = 'M'
    model.value.orgId = res.data
  } else {
    throw new Error(res.msg)
  }
}
const createOrUpdate = async (title: string) => {
  submitting.value = true
  try {
    const res = await api.post(`${baseApi}/${tenantId.value}`, model.value)
    if (res.code === 0) {
      ElMessage.success(`${title}租户管理员成功`)
      visible.value = false
    } else {
      ElMessage.error(res.msg)
    }
  } finally {
    submitting.value = false
  }
}

const visible = ref(false)
const formRef = ref()
watch(visible, value => {
  if (!value) {
    formRef.value?.resetFields()
  }
})
const hidePassword = ref(false)
defineExpose({
  open: async (id?: string) => {
    tenantId.value = id
    loadData(id).then(() => {
      if (model.value.id) hidePassword.value = true
      if (!model.value.orgId) setOrgId()
    })
    visible.value = true
  }
})

const rules: FormRules = {
  orgId: { required: true, message: '不能为空', whitespace: true },
  username: [
    { required: true, message: '不能为空', whitespace: true },
    { validator: checkUsernameDuplicate, trigger: 'blur' }
  ],
  accountStatus: { required: true, message: '不能为空', whitespace: true },
  realname: { required: true, message: '不能为空', whitespace: true },
  email: {
    type: 'email',
    message: '请输入正确的邮箱地址',
    trigger: ['blur', 'change']
  },
  mobilePhone: {
    pattern: /^1[3-9]\d{9}$|^0\d{2,3}-\d{7,8}$|^(\+[1-9]{1}[0-9]{3,14})?([0-9]{9,14})$/,
    message: '请输入正确的手机号',
    trigger: ['blur', 'change']
  }
}
</script>
<template>
  <el-dialog v-model="visible" width="60%" title="管理员配置">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="model.username" placeholder="请输入用户名" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item
            prop="password"
            label="密码"
            :rules="hidePassword ? [] : { required: true, message: '不能为空', whitespace: true }"
            @click="hidePassword = false"
          >
            <el-button v-if="hidePassword">修改密码</el-button>
            <el-input v-else v-model="model.password" placeholder="请输入密码" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="accountStatus" label="账号状态">
            <el-select v-model="model.accountStatus" placeholder="请选择账号状态">
              <el-option
                v-for="item in relatedData.accountStatusOptions"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="realname" label="姓名">
            <el-input v-model="model.realname" placeholder="请输入姓名" />
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
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="createOrUpdate(model.id ? '更新' : '添加')">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>
