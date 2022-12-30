<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import { checkPasswordRule } from './check-password'
import PasswordStrength from './PasswordStrength.vue'
import useAuthStore from '@/store/auth'

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
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

// 注册表单校验规则
const rules = reactive({
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { validator: validateNewPassword, trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入原密码', trigger: 'blur' },
    { validator: validateCheckPassword, trigger: 'blur' }
  ]
})

// 提交校验方法
const loading = ref(false)
const submitForm = (formEl: FormInstance | undefined) => {
  loading.value = true
  if (!formEl) return
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
        .catch(err => ElMessage.error(err.msg || err.message || '更新失败！'))
        .finally(() => (loading.value = false))
    }
  })
}
</script>

<template>
  <el-card shadow="never" header="修改密码">
    <el-form ref="ruleFormRef" :model="form" :rules="rules" label-width="120px" style="margin-top: 20px; width: 50%">
      <el-form-item label="当前密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="password">
        <el-input v-model="form.password" type="password" show-password />
      </el-form-item>
      <password-strength :password="form.password" />
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" show-password />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="submitForm(ruleFormRef)">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
