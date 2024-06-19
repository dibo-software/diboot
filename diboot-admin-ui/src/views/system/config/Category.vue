<script setup lang="ts">
import type { SystemConfig } from '@/views/system/config/type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baesApi = '/system-config'

const editable = checkPermission('update')

const props = defineProps<{ category: string }>()

const configList = ref<SystemConfig[]>()

const loading = ref(false)

api
  .get(`${baesApi}`, props)
  .then(res => (configList.value = res.data))
  .catch(err => ElMessage.error(err.message || err.msg || i18n.t('config.fetchException')))
  .finally(() => (loading.value = false))

const submitting = ref(false)
const save = () => {
  submitting.value = true
  api
    .put(`${baesApi}`, configList.value)
    .then(res => ElMessage.success(res.msg))
    .catch(err => ElMessage.error(err.message || err.msg || i18n.t('config.saveException')))
    .finally(() => (submitting.value = false))
}
</script>

<template>
  <el-scrollbar>
    <el-form v-loading="loading" :disabled="!editable" label-width="150px" style="width: calc(100% - 150px)">
      <el-form-item v-for="config in configList" :key="config.id" :label="config.propKey">
        <el-input v-if="config.dataType === 'text'" v-model="config.propValue" />
        <el-input v-if="config.dataType === 'textarea'" v-model="config.propValue" type="textarea" />
        <el-input-number
          v-if="config.dataType === 'number'"
          :model-value="Number(config.propValue)"
          @update:model-value="config.propValue = `${$event}`"
        />
        <el-switch v-if="config.dataType === 'boolean'" v-model="config.propValue" />
      </el-form-item>
    </el-form>
    <div v-if="editable" style="margin-left: 150px">
      <el-button type="primary" :loading="submitting" @click="save">{{ $t('button.save') }}</el-button>
    </div>
  </el-scrollbar>
</template>

<style scoped></style>
