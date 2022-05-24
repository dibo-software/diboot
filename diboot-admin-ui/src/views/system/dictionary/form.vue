<script setup lang="ts" name="DictionaryForm">
import useForm, { BaseFormLoader } from '@/hooks/form'
import { FormInstance, FormRules } from 'element-plus'
import { ApiData } from '@/utils/request'

interface FormModel {
  id?: string
  type: string
  itemName: string
  itemValue?: string
  description?: string
  color?: string
  children?: FormModel[]
}
type Props = {
  type?: string
  width?: number | string
}
withDefaults(defineProps<Props>(), {
  type: 'modal',
  width: 720
})

const emit = defineEmits(['complete'])

const formLabelWidth = '120px'
const predefineColors = ref(['#ff4500', '#ff8c00', '#ffd700', '#90ee90', '#00ced1', '#1e90ff', '#c71585', '#c71585'])
const initModel = {
  type: '',
  itemName: '',
  itemValue: '',
  description: '',
  children: [
    {
      type: '',
      itemName: '',
      itemValue: '',
      color: ''
    }
  ]
}
const rules = reactive<FormRules>({
  itemName: [
    {
      required: true,
      message: '请输入字典名称',
      trigger: 'change'
    }
  ],
  type: [
    {
      required: true,
      message: '请输入字典编码',
      trigger: 'change'
    }
  ]
})

// 使用form的hooks
class DictFormLoader extends BaseFormLoader<FormModel> {
  public async enhance(values: FormModel): Promise<FormModel> {
    const { type, children } = values
    if (children && children.length > 0) {
      children.forEach(item => {
        item.type = type
      })
    }
    return super.enhance(values)
  }

  public afterSubmitSuccess(res: ApiData<FormModel>) {
    emit('complete', res.data)
    super.afterSubmitSuccess(res)
  }
}
const { pageLoader, title, model, visible } = useForm<FormModel>({
  pageLoader: new DictFormLoader(),
  options: {
    baseApi: '/dictionary',
    model: _.cloneDeep(initModel)
  }
})

// 构建表单ref
const formRef = ref<FormInstance>()

// 定义开启表单弹窗函数
const open = async (id?: string) => {
  await pageLoader.open(id)
}

// 添加数据字典条目
const addItem = () => {
  validateChildren()
  model?.value?.children && model.value.children.push(_.cloneDeep(initModel))
}

// 移除数据字典条目
const removeItem = (index: number) => {
  validateChildren()
  model?.value?.children && model.value.children.splice(index, 1)
}

const validateChildren = () => {
  if (!model || !model.value) {
    ElMessage({
      message: '参数错误',
      grouping: true,
      type: 'warning'
    })
    throw new Error('参数错误')
  }
  if (model.value?.children == null) {
    model.value.children = []
  }
}

// 释放系列函数供外部调用
defineExpose({
  open
})
</script>
<template>
  <el-dialog v-model="visible" :width="width" :title="title">
    <el-form v-if="model" ref="formRef" :model="model" :rules="rules" label-position="top">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="字典名称" prop="itemName" :label-width="formLabelWidth">
            <el-input v-model="model.itemName" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="字典编码" prop="type" :label-width="formLabelWidth">
            <el-input v-model="model.type" autocomplete="off" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="字典备注" :label-width="formLabelWidth">
        <el-input v-model="model.description" :rows="2" type="textarea" placeholder="请输入备注" />
      </el-form-item>
      <el-table v-if="model?.children" :data="model.children" style="width: 100%">
        <el-table-column width="250">
          <template #header>
            <span class="required-flag">*</span>
            条目名称
          </template>
          <template #default="scope">
            <el-form-item
              :prop="`children.${scope.$index}.itemName`"
              :rules="{
                required: true,
                message: '请输入条目名称',
                trigger: 'blur'
              }"
            >
              <el-input v-model="scope.row.itemName" placeholder="条目名称" />
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column width="250">
          <template #header>
            <span class="required-flag">*</span>
            条目编码
          </template>
          <template #default="scope">
            <el-form-item
              :prop="`children.${scope.$index}.itemValue`"
              :rules="{
                required: true,
                message: '请输入条目编码',
                trigger: 'blur'
              }"
            >
              <el-input v-model="scope.row.itemValue" placeholder="条目编码" />
            </el-form-item>
          </template>
        </el-table-column>
        <el-table-column label="条目颜色" width="100">
          <template #default="scope">
            <el-color-picker v-model="scope.row.color" :predefine="predefineColors" />
          </template>
        </el-table-column>
        <el-table-column>
          <template #header>
            <el-button size="small" type="primary" @click="addItem">添加</el-button>
          </template>
          <template #default="scope">
            <el-button size="small" type="danger" @click="removeItem(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="pageLoader.onSubmit(formRef)">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<style lang="scss">
.required-flag {
  color: var(--el-color-danger);
  font-weight: 400;
}
</style>
