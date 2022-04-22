<script setup lang="ts">
import { FormInstance } from 'element-plus'
import useUserStore from '@/store/user'

const userStore = useUserStore()

const formRef = ref<FormInstance>()
const form = reactive({ username: 'admin', password: '123456' })
const rules = {
  username: { required: true, message: '不能为空', trigger: 'blur' },
  password: { required: true, message: '不能为空', trigger: 'blur' }
}
const router = useRouter()

const submitForm = async () => {
  await formRef.value?.validate(valid => {
    if (valid) {
      userStore.login(form).then(() => {
        router.push('/')
      })
    }
  })
}
</script>

<template>
  <div class="content">
    <h2 style="text-align: center">Diboot Admin UI</h2>
    <el-form ref="formRef" :model="form" :rules="rules">
      <el-form-item prop="username" label="">
        <el-input v-model="form.username">
          <template #prefix>
            <span style="width: 50px">用户名</span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="form.password" show-password>
          <template #prefix>
            <span style="width: 50px">密 码 </span>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item>
        <el-button style="width: 100%" type="primary" plain @click="submitForm">登 陆</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.content {
  width: 300px;
  margin: 25vh auto;
}
</style>
