<script setup lang="ts">
import type { I18nConfig } from './type'
import type { FormInstance } from 'element-plus'
import { useI18n } from 'vue-i18n'

const baseApi = '/i18n-config'

const i18n = useI18n()
const locales = i18n.availableLocales.map(e => e.replace(/-/g, '_'))

const initData: Partial<I18nConfig>[] = Array.of(...locales).map(locale => ({ language: locale }))

const loading = ref(false)
const model: {
  i18nCode?: string
  list: Partial<I18nConfig>[]
} = reactive({
  list: []
})

const visible = ref(false)
// 表单
const formRef = ref<FormInstance>()
const sortI18n = () => {
  const zh = model.list.find(item => item.language === 'zh_CN')
  const en = model.list.find(item => item.language === 'en')
  const rest = model.list.filter(item => item.language !== 'zh_CN' && item.language !== 'en')
  const arr: Partial<I18nConfig>[] = []
  if (zh) arr.push(zh)
  if (en) arr.push(en)
  if (rest && rest.length > 0) rest.forEach(item => arr.push(item))
  model.list = arr
}
defineExpose({
  open: (code?: string) => {
    model.i18nCode = code
    formRef.value?.resetFields()
    if (code) {
      loading.value = true
      api
        .get<I18nConfig[]>(`${baseApi}/${code}`)
        .then(res => {
          if (res.data) model.list = res.data
          else model.list = initData
          const type = model.list.length ? model.list[0]?.type : undefined
          Array.of(...locales)
            .filter(locale => !model.list.some(e => e?.language === locale))
            .forEach(locale => model.list.push({ type, language: locale, code }))
          model.list.sort(
            (a, b) => locales.findIndex(e => e === a?.language) - locales.findIndex(e => e === b?.language)
          )
          model.list = model.list.filter(item => !!locales.find(e => e === item?.language))
          sortI18n()
        })
        .catch(err => {
          ElNotification.error({
            title: i18n.t('i18nConfig.fetchDataListError'),
            message: err.msg || err.message || err
          })
        })
        .finally(() => (loading.value = false))
    } else {
      model.list = initData
      sortI18n()
    }
    visible.value = true
  }
})

const emit = defineEmits<{
  (e: 'complete', code?: string): void
}>()

watch(
  () => model.i18nCode,
  code => {
    model.list.forEach(e => {
      if (e) e.code = code
    })
  }
)

const { submitting, submitPost } = useForm({
  baseApi,
  successCallback() {
    emit('complete', model.i18nCode)
    visible.value = false
  }
})

const checkCodeDuplicate = (rule: unknown, value: unknown, callback: (error?: string | Error) => void) => {
  if (value) {
    api
      .post(
        `${baseApi}/check-code-duplicate/${value}`,
        model.list.filter(e => e.id).map(e => e.id)
      )
      .then(() => {
        callback()
      })
      .catch(err => {
        callback(err.msg || err)
      })
  }
}
</script>

<template>
  <el-dialog v-model="visible" :title="$t('i18nConfig.internationalTranslation')">
    <el-form ref="formRef" v-loading="loading" :model="model" :label-width="$i18n.locale === 'en' ? '150px' : '100px'">
      <el-form-item
        prop="i18nCode"
        :label="$t('i18nConfig.i18nCode')"
        :rules="[
          { required: true, message: i18n.t('rules.notnull'), whitespace: true },
          { pattern: /^[A-Za-z0-9.-_-]+$/, message: $t('i18nConfig.rules.i18nCode'), trigger: 'blur' },
          { validator: checkCodeDuplicate, trigger: 'blur' }
        ]"
      >
        <el-input v-model="model.i18nCode" :disabled="model.list.some(e => e.type === 'SYSTEM')" />
      </el-form-item>
      <el-divider>{{ $t('i18nConfig.translate') }}</el-divider>
      <el-form-item
        v-for="(item, index) in model.list"
        :key="index"
        :prop="`list.${index}.content`"
        :label="$t('language', {}, { locale: item?.language?.replace(/_/g, '-') })"
        :rules="{ required: true, message: i18n.t('rules.notnull'), whitespace: true }"
      >
        <el-input v-model="item.content" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="submitPost(model.list, formRef)">{{
        $t('button.save')
      }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-form-item {
  margin-bottom: 18px;
}
</style>
