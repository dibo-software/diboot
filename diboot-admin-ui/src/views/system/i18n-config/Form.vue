<script setup lang="ts">
import type { I18nConfig } from './type'
import type { FormInstance } from 'element-plus'
import { useI18n } from 'vue-i18n'

const baseApi = '/i18n-config'

const i18n = useI18n()
const locales = i18n.availableLocales.map(e => e.replace(/-/g, '_'))

const initData: Partial<I18nConfig>[] = Array.of(...locales).map(locale => ({ language: locale }))

const loading = ref(false)
const modle: {
  i18nCode?: string
  list: Partial<I18nConfig>[]
} = reactive({
  list: []
})

const visible = ref(false)
// 表单
const formRef = ref<FormInstance>()

defineExpose({
  open: (code?: string) => {
    modle.i18nCode = code
    formRef.value?.resetFields()
    if (code) {
      loading.value = true
      api
        .get<I18nConfig[]>(`${baseApi}/${code}`)
        .then(res => {
          if (res.data) modle.list = res.data
          else modle.list = initData
          const type = modle.list.length ? modle.list[0]?.type : undefined
          Array.of(...locales)
            .filter(locale => !modle.list.some(e => e?.language === locale))
            .forEach(locale => modle.list.push({ type, language: locale, code }))
          modle.list.sort(
            (a, b) => locales.findIndex(e => e === a?.language) - locales.findIndex(e => e === b?.language)
          )
        })
        .catch(err => {
          ElNotification.error({
            title: '获取列表数据失败',
            message: err.msg || err.message || err
          })
        })
        .finally(() => (loading.value = false))
    } else {
      modle.list = initData
    }
    visible.value = true
  }
})

const emit = defineEmits<{
  (e: 'complete', code?: string): void
}>()

watch(
  () => modle.i18nCode,
  code => {
    modle.list.forEach(e => {
      if (e) e.code = code
    })
  }
)

const { submitting, submitPost } = useForm({
  baseApi,
  successCallback() {
    emit('complete', modle.i18nCode)
    visible.value = false
  }
})

const checkCodeDuplicate = (rule: unknown, value: unknown, callback: (error?: string | Error) => void) => {
  if (value) {
    api
      .post(
        `${baseApi}/check-code-duplicate/${value}`,
        modle.list.filter(e => e.id).map(e => e.id)
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
  <el-dialog v-model="visible" title="国际化翻译">
    <el-form ref="formRef" v-loading="loading" :model="modle" label-width="80px">
      <el-form-item
        prop="i18nCode"
        label="资源标识"
        :rules="[
          { required: true, message: '不能为空', whitespace: true },
          { pattern: /^\w+$/, message: '只可以输入字母数字下划线', trigger: 'blur' },
          { validator: checkCodeDuplicate, trigger: 'blur' }
        ]"
      >
        <el-input v-model="modle.i18nCode" :disabled="modle.list.some(e => e.type === 'SYSTEM')" />
      </el-form-item>
      <el-divider>翻译</el-divider>
      <el-form-item
        v-for="(item, index) in modle.list"
        :key="index"
        :prop="`list.${index}.content`"
        :label="$t('language', {}, { locale: item?.language?.replace(/_/g, '-') })"
        :rules="{ required: true, message: '不能为空', whitespace: true }"
      >
        <el-input v-model="item.content" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submitPost(modle.list, formRef)">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-form-item {
  margin-bottom: 18px;
}
</style>
