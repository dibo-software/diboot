<script setup lang="ts">
import type { I18nConfig } from './type'
import type { FormInstance } from 'element-plus'
import { defineEmits } from 'vue'
import { useI18n } from 'vue-i18n'

const baseApi = '/i18n-config'

const i18n = useI18n()
const locales = i18n.availableLocales.map(e => e.replaceAll('-', '_'))

const initData: Partial<I18nConfig>[] = Array.of(...locales).map(locale => ({ language: locale }))
const { loadData, loading, model } = useDetail<Partial<I18nConfig>[]>(baseApi, initData)

const visible = ref(false)
const i18nCode = ref()
// 表单
const formRef = ref<FormInstance>()

defineExpose({
  open: (code: string) => {
    i18nCode.value = code
    formRef.value?.resetFields()
    initData.forEach(e => (e.code = code))
    loadData(code).then(() => {
      const type = model.value && model.value.length ? model.value[0]?.type : undefined
      Array.of(...locales)
        .filter(locale => !model.value?.some(e => e?.language === locale))
        .forEach(locale => model.value.push({ type, language: locale, code }))
      model.value.sort((a, b) => locales.findIndex(e => e === a?.language) - locales.findIndex(e => e === b?.language))
    })
    visible.value = true
  }
})

const emit = defineEmits<{
  (e: 'complete', code: string): void
}>()

watch(i18nCode, code => {
  model.value.forEach(e => {
    if (e) e.code = code
  })
})

const { submitting, submitPost } = useForm({
  baseApi,
  successCallback() {
    emit('complete', i18nCode.value)
    visible.value = false
  }
})
</script>

<template>
  <el-dialog v-model="visible" title="国际化翻译">
    <el-form ref="formRef" v-loading="loading" :model="model" label-width="80px">
      <el-form-item prop="0.code" label="资源标识" :rules="{ required: true, message: '不能为空', whitespace: true }">
        <el-input
          v-model="model[0].code"
          :disabled="model.some(e => e.type === 'SYSTEM')"
          @change="value => (i18nCode = value)"
        />
      </el-form-item>
      <el-divider>翻译</el-divider>
      <el-form-item
        v-for="(item, index) in model"
        :key="index"
        :prop="`${index}.content`"
        :label="$t('language', null, { locale: item.language.replaceAll('_', '-') })"
        :rules="{ required: true, message: '不能为空', whitespace: true }"
      >
        <el-input v-model="item.content" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitPost(model, formRef)">提交</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-form-item {
  margin-bottom: 18px;
}
</style>
