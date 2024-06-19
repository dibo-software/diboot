<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { MessageTemplate } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/message-template'

const { loadData, loading, model } = useDetail<MessageTemplate>(baseApi)

const title = ref('')

const visible = ref(false)

const templateVariableList = ref<Array<string>>()
defineExpose({
  open: (id?: string) => {
    title.value = id ? i18n.t('title.update') : i18n.t('title.create')
    loadData(id)
    visible.value = true
    if (!templateVariableList.value)
      api.get<Array<string>>(`${baseApi}/variable-list`).then(res => (templateVariableList.value = res.data))
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

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const checkTempCodeDuplicate = checkValue(`${baseApi}/check-temp-code-duplicate`, 'code', () => model.value?.id)

const rules: FormRules = {
  title: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  code: [
    { required: true, message: i18n.t('rules.notnull'), whitespace: true },
    { validator: checkTempCodeDuplicate, trigger: 'blur' }
  ],
  content: { required: true, message: i18n.t('rules.notnull'), whitespace: true }
}
/**
 * 向内容中插入变量
 * @param variable
 */
const appendVariable = (variable?: string) => {
  if (model.value?.content) model.value.content = model.value?.content + variable
  else model.value.content = variable
}
</script>

<template>
  <el-dialog v-model="visible" :title="title">
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '130px' : '80px'"
    >
      <el-form-item prop="title" :label="$t('messageTemplate.title')">
        <el-input v-model="model.title" :placeholder="$t('messageTemplate.placeholder.title')" />
      </el-form-item>
      <el-form-item prop="code" :label="$t('messageTemplate.code')">
        <el-input v-model="model.code" :placeholder="$t('messageTemplate.placeholder.code')" />
      </el-form-item>
      <el-form-item prop="appModule" :label="$t('messageTemplate.appModule')">
        <el-input v-model="model.appModule" :placeholder="$t('messageTemplate.placeholder.appModule')" />
      </el-form-item>
      <el-space fill style="width: 100%">
        <el-alert type="info" show-icon :closable="false">
          {{ $t('messageTemplate.selectableVariables') }}：
          <el-tag
            v-for="(item, index) in templateVariableList"
            :key="index"
            :title="$t('messageTemplate.addVariable')"
            class="variable-tag"
            @click="appendVariable(item)"
          >
            {{ item }}
          </el-tag>
        </el-alert>
        <el-form-item prop="content" :label="$t('messageTemplate.content')">
          <el-input
            v-model="model.content"
            type="textarea"
            :rows="5"
            :placeholder="$t('messageTemplate.placeholder.content')"
          />
        </el-form-item>
      </el-space>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="submit(model, formRef)">{{
        $t('button.save')
      }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.variable-tag {
  margin: 0 5px 5px 0;
  cursor: pointer;
}
</style>
