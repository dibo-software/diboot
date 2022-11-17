<template>
  <el-card shadow="never" header="个人信息">
    <el-form :model="form" label-width="120px" style="margin-top: 20px; width: 50%">
      <el-form-item label="姓名">
        <el-input v-model="form.realname" />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="form.mobilePhone" />
      </el-form-item>
      <el-form-item label="邮箱">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item label="性别">
        <el-select v-model="form.gender" placeholder="请选择">
          <el-option
            v-for="item in relatedData.genderOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save">保存</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script lang="ts" setup>
import useAuthStore from '@/store/auth'
import { reactive } from 'vue'

const baseApi = '/iam/user'

const authStore: any = useAuthStore()
const { relatedData, initRelatedData } = useOption({ dict: ['GENDER'] })
initRelatedData()

const form = reactive({
  realname: authStore.realname,
  mobilePhone: authStore.info?.mobilePhone,
  gender: authStore.info?.gender,
  email: authStore.info?.email
})

const save = async () => {
  const data = reactive(Object.assign(authStore.info, form))
  const res = await api.post<string>(`${baseApi}/update-current-user-info`, data)
  if (res.code === 0) {
    ElMessage.success(res.msg)
    await authStore.getInfo()
  }
}
</script>

<style scoped></style>
