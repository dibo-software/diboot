<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import Draggable from 'vuedraggable'
import { Sort } from '@element-plus/icons-vue'
import type { Dictionary } from '@/views/system/dictionary/type'
import { checkValue } from '@/utils/validate-form'

const baseApi = '/dictionary'

type Props = {
  type?: string
  width?: number | string
}
withDefaults(defineProps<Props>(), {
  type: 'modal',
  width: 720
})

const emit = defineEmits(['complete'])

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

// 加载表单信息
const { loadData, loading, model } = useDetail<Dictionary>(baseApi)
const title = ref('')
const visible = ref(false)
const open = async (id?: string) => {
  title.value = id ? '更新' : '新建'
  visible.value = true
  await loadData(id)
  if (!model.value?.children) {
    model.value.children = []
  }
}

const formLabelWidth = '120px'
const predefineColors = ref(['#ff4500', '#ff8c00', '#ffd700', '#90ee90', '#00ced1', '#1e90ff', '#c71585', '#c71585'])

const checkTypeDuplicate = checkValue(`${baseApi}/check-type-duplicate`, 'type', () => model.value?.id)

const rules = reactive<FormRules>({
  itemName: [{ required: true, message: '请输入字典名称', trigger: 'change' }],
  type: [
    { required: true, message: '请输入字典编码', trigger: 'change' },
    { validator: checkTypeDuplicate, trigger: 'blur' }
  ]
})

const { submit, submitting } = useForm({
  baseApi,
  async afterValidate() {
    const { type, children } = model.value
    if (children && children.length > 0) {
      children.forEach(item => {
        item.type = type
      })
    }
  },
  successCallback(id) {
    emit('complete', id)
    visible.value = isContinueAdd.value
    isContinueAdd.value && resetFormContent()
  }
})

// 构建表单ref
const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) {
    resetFormContent()
  }
})

// 清空表单所有内容
const resetFormContent = () => {
  formRef.value?.resetFields()
  model.value.children = []
}

// 保存之前判断是否确认并继续添加
const beforeSubmit = (value: boolean) => {
  isContinueAdd.value = value
  submit(model.value, formRef.value)
}

// 添加数据字典条目
const addItem = () => {
  validateChildren()
  model?.value?.children && model.value.children.push({ itemName: '', itemValue: '' })
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

defineExpose({ open })
</script>
<template>
  <el-dialog v-model="visible" :width="width" :title="title">
    <el-form v-if="model" ref="formRef" v-loading="loading" :model="model" :rules="rules" label-position="top">
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
      <br />
      <template v-if="model?.children">
        <table class="children-table">
          <thead>
            <tr>
              <th>排序</th>
              <th><span class="required-flag">*</span> 条目名称</th>
              <th><span class="required-flag">*</span> 条目编码</th>
              <th>条目颜色</th>
              <!--              <th>国际化</th>-->
              <th>
                <el-button size="small" type="primary" @click="addItem">添加</el-button>
              </th>
            </tr>
          </thead>
          <draggable
            v-model="model.children"
            tag="tbody"
            item-key="index"
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
                <td class="color-picker-td">
                  <el-color-picker
                    v-model="(element.extension ? element.extension : (element.extension = {})).color"
                    :predefine="predefineColors"
                  />
                </td>
                <!--                <td>-->
                <!--                  <i18n-selector v-model="element.itemNameI18n" style="position: relative; top: -8px" />-->
                <!--                </td>-->
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
        <el-button v-if="!model.id" type="primary" :loading="submitting" @click="beforeSubmit(true)"
          >保存并继续
        </el-button>
        <el-button type="primary" :loading="submitting" @click="beforeSubmit(false)">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<style scoped lang="scss">
.required-flag {
  color: var(--el-color-danger);
  font-weight: 400;
}

.children-table {
  border-spacing: 0;
  width: 100%;

  th {
    padding-bottom: 12px;
  }

  td {
    text-align: center;
    border-top: 2px solid transparent;
  }

  td > * {
    margin-top: 2px;
    margin-bottom: 18px;
  }

  .color-picker-td {
    padding-top: 2px;
    padding-bottom: 18px;
  }

  .drag-handle {
    cursor: move;
  }

  .sortable-ghost {
    td {
      border-top: 2px solid var(--el-color-primary);
      background: #efefef;

      * {
        visibility: hidden;
      }
    }
  }
}
</style>
