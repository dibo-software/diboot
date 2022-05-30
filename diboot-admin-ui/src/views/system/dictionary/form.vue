<script setup lang="ts" name="DictionaryForm">
import useForm, { BaseFormLoader } from '@/hooks/form'
import { FormInstance, FormRules } from 'element-plus'
import { ApiData } from '@/utils/request'
import draggable from 'vuedraggable'
import { Sort } from '@element-plus/icons-vue'

interface FormModel {
  id?: string
  type: string
  itemName: string
  itemValue?: string
  description?: string
  color?: string
  sortId?: number
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
  console.log('removeItem', index)
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
      <template v-if="model?.children">
        <table class="children-table">
          <thead>
            <tr>
              <th>排序</th>
              <th><span class="required-flag">*</span> 条目名称</th>
              <th><span class="required-flag">*</span> 条目编码</th>
              <th>条目颜色</th>
              <th>
                <el-button size="small" type="primary" @click="addItem">添加</el-button>
              </th>
            </tr>
          </thead>
          <draggable
            v-model="model.children"
            tag="tbody"
            item-key="itemValue"
            ghost-class="sortable-ghost"
            handle=".drag-handle"
          >
            <template #item="{ element, index }">
              <tr>
                <td>
                  <el-button class="drag-handle" plain :icon="Sort" />
                </td>
                <td>
                  <el-form-item
                    :prop="`children.${index}.itemName`"
                    :rules="{
                      required: true,
                      message: '请输入条目名称',
                      trigger: 'blur'
                    }"
                  >
                    <el-input v-model="element.itemName" placeholder="条目名称" />
                  </el-form-item>
                </td>
                <td>
                  <el-form-item
                    :prop="`children.${index}.itemValue`"
                    :rules="{
                      required: true,
                      message: '请输入条目编码',
                      trigger: 'blur'
                    }"
                  >
                    <el-input v-model="element.itemValue" placeholder="条目编码" />
                  </el-form-item>
                </td>
                <td>
                  <el-color-picker v-model="element.color" :predefine="predefineColors" />
                </td>
                <td>
                  <el-button size="small" type="danger" @click="removeItem(index)">删除</el-button>
                </td>
              </tr>
            </template>
          </draggable>
        </table>
      </template>
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
.children-table {
  width: 100%;
  th {
    padding-bottom: 12px;
  }
  td {
    text-align: center;
  }
  td > * {
    margin-bottom: 18px;
  }
  .drag-handle {
    cursor: move;
  }
}
</style>
