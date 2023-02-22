<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { MessageTemplate } from './type'
import { checkValue } from '@/utils/validate-form'

const baseApi = '/message-template'

const { loadData, loading, model } = useDetail<MessageTemplate>(baseApi)

const title = ref('')

const visible = ref(false)

const templateVariableList = ref<Array<string>>()
defineExpose({
  open: (id?: string) => {
    title.value = id ? '更新消息通知模板' : '新建消息通知模板'
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
  title: { required: true, message: '不能为空', whitespace: true },
  code: [
    { required: true, message: '不能为空', whitespace: true },
    { validator: checkTempCodeDuplicate, trigger: 'blur' }
  ],
  content: { required: true, message: '不能为空', whitespace: true }
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
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-form-item prop="title" label="模板名称">
        <el-input v-model="model.title" />
      </el-form-item>
      <el-form-item prop="code" label="模板编码">
        <el-input v-model="model.code" />
      </el-form-item>
      <el-form-item prop="appModule" label="业务模块">
        <el-input v-model="model.appModule" />
      </el-form-item>
      <el-space fill style="width: 100%">
        <el-alert type="info" show-icon :closable="false">
          可选变量：
          <el-tag
            v-for="(item, index) in templateVariableList"
            :key="index"
            title="添加此变量"
            class="variable-tag"
            @click="appendVariable(item)"
          >
            {{ item }}
          </el-tag>
        </el-alert>
        <el-form-item prop="content" label="消息内容">
          <el-input v-model="model.content" type="textarea" :rows="5" />
        </el-form-item>
      </el-space>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit(model, formRef)">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.variable-tag {
  margin: 0 5px 5px 0;
  cursor: pointer;
}
</style>
