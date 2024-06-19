<script setup lang="ts">
import type { UserModel } from '@/views/org-structure/user/type'
import { checkValue } from '@/utils/validate-form'
import type { FormRules } from 'element-plus'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
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
  orgId: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  username: [
    { required: true, message: i18n.t('rules.notnull'), whitespace: true },
    { validator: checkUsernameDuplicate, trigger: 'blur' }
  ],
  accountStatus: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  realname: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  email: {
    type: 'email',
    message: i18n.t('user.rules.email'),
    trigger: ['blur', 'change']
  },
  mobilePhone: {
    pattern: /^1[3-9]\d{9}$|^0\d{2,3}-\d{7,8}$|^(\+[1-9]{1}[0-9]{3,14})?([0-9]{9,14})$/,
    message: i18n.t('user.rules.mobilePhone'),
    trigger: ['blur', 'change']
  }
}
</script>
<template>
  <el-dialog v-model="visible" width="60%" :title="$t('tenantInfo.adminForm.title')">
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '120px' : '80px'"
    >
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="username" :label="$t('user.username')">
            <el-input v-model="model.username" :placeholder="$t('user.placeholder.username')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item
            prop="password"
            :label="$t('user.password')"
            :rules="hidePassword ? [] : { required: true, message: i18n.t('rules.notnull'), whitespace: true }"
            @click="hidePassword = false"
          >
            <el-button v-if="hidePassword">{{ $t('user.modifyPassword') }}</el-button>
            <el-input v-else v-model="model.password" :placeholder="$t('user.placeholder.password')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="accountStatus" :label="$t('user.accountStatus')">
            <el-select v-model="model.accountStatus" :placeholder="$t('user.placeholder.accountStatus')">
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
          <el-form-item prop="realname" :label="$t('user.realname')">
            <el-input v-model="model.realname" :placeholder="$t('user.placeholder.realname')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="mobilePhone" :label="$t('user.mobilePhone')">
            <el-input v-model="model.mobilePhone" :placeholder="$t('user.placeholder.mobilePhone')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="email" :label="$t('user.email')">
            <el-input v-model="model.email" :placeholder="$t('user.placeholder.email')" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button
        type="primary"
        :loading="submitting"
        @click="createOrUpdate(model.id ? i18n.t('title.update') : i18n.t('title.create'))"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>
