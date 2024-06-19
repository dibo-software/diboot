<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import Draggable from 'vuedraggable'
import { Sort } from '@element-plus/icons-vue'
import type { Dictionary } from '@/views/system/dictionary/type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
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
  title.value = id ? i18n.t('title.update') : i18n.t('title.create')
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
  itemName: [{ required: true, message: i18n.t('dictionary.rules.itemName'), trigger: 'change' }],
  type: [
    { required: true, message: i18n.t('dictionary.rules.type'), trigger: 'change' },
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
      message: i18n.t('dictionary.paramsError'),
      grouping: true,
      type: 'warning'
    })
    throw new Error(i18n.t('dictionary.paramsError'))
  }
  if (model.value?.children == null) {
    model.value.children = []
  }
}
const enableI18n = import.meta.env.VITE_APP_ENABLE_I18N === 'true'
defineExpose({ open })
</script>
<template>
  <el-dialog v-model="visible" :width="width" :title="title" append-to-body>
    <el-form v-if="model" ref="formRef" v-loading="loading" :model="model" :rules="rules" label-position="top">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item :label="$t('dictionary.itemName')" prop="itemName" :label-width="formLabelWidth">
            <el-input v-model="model.itemName" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$t('dictionary.type')" prop="type" :label-width="formLabelWidth">
            <el-input v-model="model.type" autocomplete="off" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item :label="$t('dictionary.description')" :label-width="formLabelWidth">
        <el-input
          v-model="model.description"
          :rows="2"
          type="textarea"
          :placeholder="`${$t('placeholder.input')} ${$t('dictionary.description')}`"
        />
      </el-form-item>
      <br />
      <template v-if="model?.children">
        <table class="children-table">
          <thead>
            <tr>
              <th>{{ $t('dictionary.item.sort') }}</th>
              <th><span class="required-flag">*</span> {{ $t('dictionary.item.itemName') }}</th>
              <th><span class="required-flag">*</span> {{ $t('dictionary.item.itemValue') }}</th>
              <th>{{ $t('dictionary.item.color') }}</th>
              <th v-if="enableI18n">{{ $t('dictionary.item.internationalization') }}</th>
              <th>
                <el-button size="small" type="primary" @click="addItem">{{ $t('operation.add') }}</el-button>
              </th>
            </tr>
          </thead>
          <div style="height: 10px" />
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
                      message: $t('dictionary.item.rules.itemName'),
                      trigger: 'blur'
                    }"
                  >
                    <el-input v-model="element.itemName" :placeholder="$t('dictionary.item.itemName')" />
                  </el-form-item>
                </td>
                <td>
                  <el-form-item
                    :prop="`children.${index}.itemValue`"
                    :rules="{
                      required: true,
                      message: $t('dictionary.item.rules.itemValue'),
                      trigger: 'blur'
                    }"
                  >
                    <el-input v-model="element.itemValue" :placeholder="$t('dictionary.item.itemValue')" />
                  </el-form-item>
                </td>
                <td>
                  <el-color-picker
                    v-model="(element.extension ? element.extension : (element.extension = {})).color"
                    :predefine="predefineColors"
                  />
                </td>
                <td v-if="enableI18n">
                  <i18n-selector v-model="element.itemNameI18n" style="position: relative; top: -8px" />
                </td>
                <td>
                  <el-button size="small" type="danger" @click="removeItem(index)">{{
                    $t('operation.delete')
                  }}</el-button>
                </td>
              </tr>
            </template>
          </draggable>
        </table>
      </template>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
        <el-button v-if="!model.id" type="primary" :loading="submitting" @click="beforeSubmit(true)">
          {{ $t('button.continueAdd') }}
        </el-button>
        <el-button type="primary" :loading="submitting" @click="beforeSubmit(false)">{{ $t('button.save') }}</el-button>
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
    margin-bottom: 18px;
  }

  .drag-handle {
    cursor: move;
  }

  .sortable-ghost {
    td {
      border-top: 2px solid var(--el-color-primary);
      background: var(--el-color-info-light-9);

      * {
        visibility: hidden;
      }
    }
  }
}
</style>
