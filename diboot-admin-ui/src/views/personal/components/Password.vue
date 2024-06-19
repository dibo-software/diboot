<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { checkPasswordRule } from './check-password'
import PasswordStrength from './PasswordStrength.vue'
import useAuthStore from '@/store/auth'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/iam/user'
const authStore = useAuthStore()

const form = reactive({
  oldPassword: '',
  password: '',
  confirmPassword: ''
})

const ruleFormRef = ref<FormInstance>()

// 密码校验规则
const validateNewPassword = (rule: unknown, value: string, callback: (error?: string | Error) => void) => {
  const result: string = checkPasswordRule(value)
  if (result === '校验通过') {
    callback()
  } else {
    callback(new Error(result))
  }
}

const validateCheckPassword = (rule: unknown, value: string, callback: (error?: string | Error) => void) => {
  if (value !== form.password) {
    callback(new Error(i18n.t('personal.passwordDifferent')))
  } else {
    callback()
  }
}

// 注册表单校验规则
const rules = reactive({
  oldPassword: [{ required: true, message: i18n.t('personal.rules.oldPassword'), trigger: 'blur' }],
  password: [
    { required: true, message: i18n.t('personal.rules.password'), trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: i18n.t('personal.rules.confirmPassword'), trigger: 'blur' },
    { validator: validateCheckPassword, trigger: 'blur' }
  ]
})

// 提交校验方法
const loading = ref(false)
const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  loading.value = true
  formEl.validate(valid => {
    if (valid) {
      api
        .post<string>(`${baseApi}/change-pwd`, form)
        .then(res => {
          if (res.code === 0) {
            ElMessage.success(res.msg)
            authStore.logout()
          }
        })
        .catch(err => ElMessage.error(err.msg || err.message || i18n.t('personal.updateFailed')))
        .finally(() => (loading.value = false))
    } else loading.value = false
  })
}
</script>

<template>
  <el-card shadow="never" :header="$t('user.modifyPassword')">
    <el-form
      ref="ruleFormRef"
      :model="form"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '150px' : '120px'"
      style="margin-top: 20px; width: 50%"
    >
      <el-form-item :label="$t('personal.oldPassword')" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item :label="$t('personal.newPassword')" prop="password">
        <el-input v-model="form.password" type="password" show-password />
      </el-form-item>
      <password-strength :password="form.password" />
      <el-form-item :label="$t('personal.confirmPassword')" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submitForm(ruleFormRef)">{{
          $t('button.save')
        }}</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
