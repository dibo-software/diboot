<script setup lang="ts">
import useAuthStore from '@/store/auth'
import type { UserModel } from '@/views/org-structure/user/type'
import type { OrgModel } from '@/views/org-structure/org/type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/iam/user'

const authStore = useAuthStore()
const { relatedData, initRelatedData } = useOption({ dict: ['GENDER'] })
initRelatedData()

const form = ref()
const positions = ref()
const orgName = ref()
if (authStore.info) {
  form.value = _.cloneDeep(authStore.info)
  positions.value = authStore.info.positionList?.map(position => position.name).toString()
  api
    .get<OrgModel>(`/iam/org/${authStore.info.orgId}`)
    .then(res => {
      orgName.value = res.data?.name
    })
    .catch(err => ElMessage.error(err.msg || err.message || i18n.t('personal.updateFailed')))
}

const loading = ref(false)
const save = () => {
  loading.value = true
  api
    .post<UserModel>(`${baseApi}/update-current-user-info`, form.value)
    .then(res => {
      ElMessage.success(res.msg)
      authStore.getInfo(true)
    })
    .catch(err => ElMessage.error(err.msg || err.message || i18n.t('personal.updateFailed')))
    .finally(() => (loading.value = false))
}
</script>

<template>
  <el-card shadow="never" :header="$t('personal.info')">
    <el-form
      :model="form"
      :label-width="$i18n.locale === 'en' ? '150px' : '120px'"
      style="margin-top: 20px; width: 50%"
    >
      <el-form-item :label="$t('user.realname')">
        <el-input v-model="form.realname" disabled />
      </el-form-item>
      <el-form-item :label="$t('user.gender')">
        <el-select v-model="form.gender" :placeholder="`${$t('placeholder.select')}`" disabled>
          <el-option
            v-for="item in relatedData.genderOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('user.userNumAlias')">
        <el-input v-model="form.userNum" disabled />
      </el-form-item>
      <el-form-item :label="$t('user.orgIdAlias')">
        <el-input v-model="orgName" disabled />
      </el-form-item>
      <el-form-item :label="$t('position.label')">
        <el-input v-model="positions" disabled />
      </el-form-item>
      <el-form-item :label="$t('user.mobilePhone')">
        <el-input v-model="form.mobilePhone" />
      </el-form-item>
      <el-form-item :label="$t('user.email')">
        <el-input v-model="form.email" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="save">{{ $t('button.save') }}</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
