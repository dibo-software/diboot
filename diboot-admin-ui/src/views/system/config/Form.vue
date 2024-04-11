<script setup lang="ts">
import type { FormInstance } from 'element-plus'
import type { SystemConfig } from './type'
import { checkValue } from '@/utils/validate-form'

const baseApi = '/system-config'

const { loadData, loading, model } = useDetail<SystemConfig>(baseApi, { dataType: 'text' })

const title = ref('')

const visible = ref(false)

defineExpose({
  open: (id?: string) => {
    title.value = id ? '编辑' : '新建'
    loadData(id)
    visible.value = true
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

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = isContinueAdd.value
    if (isContinueAdd.value) formRef.value?.resetFields()
  }
})

// 保存之前判断是否确认并继续添加
const onSubmit = (continueAdd = true) => {
  isContinueAdd.value = continueAdd
  submit(model.value, formRef.value)
}

const checkPropKeyDuplicate = checkValue(
  `${baseApi}/check-prop-key-duplicate`,
  'propKey',
  () => model.value?.id,
  () => ({ category: model.value.category })
)
</script>

<template>
  <el-dialog v-model="visible" :title="title" width="765">
    <el-form ref="formRef" v-loading="loading" :model="model" label-width="80px">
      <el-row>
        <el-col :span="12">
          <el-form-item prop="category" label="类别">
            <el-input v-model="model.category" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="dataType" label="填写类型">
            <el-radio-group v-model="model.dataType">
              <el-radio-button label="text">文本</el-radio-button>
              <el-radio-button label="textarea">文本域</el-radio-button>
              <el-radio-button label="number">数字</el-radio-button>
              <el-radio-button label="boolean">开关</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            prop="propKey"
            label="属性名"
            :rules="[
              { required: true, message: '不能为空', whitespace: true },
              { validator: checkPropKeyDuplicate, trigger: 'blur' }
            ]"
          >
            <el-input v-model="model.propKey" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="propValue" label="属性值">
            <el-input v-if="model.dataType === 'text'" v-model="model.propValue" />
            <el-input v-if="model.dataType === 'textarea'" v-model="model.propValue" type="textarea" />
            <el-input-number
              v-if="model.dataType === 'number'"
              :model-value="Number(model.propValue)"
              @update:model-value="model.propValue = `${$event}`"
            />
            <el-switch
              v-if="model.dataType === 'boolean'"
              :model-value="JSON.parse(`${model.propValue ?? false}`)"
              @change="model.propValue = `${$event}`"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button v-if="!model.id" type="primary" :loading="submitting" @click="onSubmit(true)"> 保存并继续 </el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit(false)">保存</el-button>
    </template>
  </el-dialog>
</template>
