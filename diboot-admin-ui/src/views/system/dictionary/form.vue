<script setup lang="ts" name="DictionaryForm">
import { defineEmits } from 'vue'
import useForm from '@/hooks/form'

interface FormModel {
  id?: string
  itemName: string
  itemValue: string
  description?: string
  color?: string
  children?: FormModel[]
}
type Props = {
  type?: string
  width?: number | string
}
const props = withDefaults(defineProps<Props>(), {
  type: 'modal',
  width: 720
})
// const emit = defineEmits(['complete'])

const formLabelWidth = '120px'
const initModel = {
  itemName: '',
  itemValue: '',
  description: '',
  children: []
}

const { pageLoader, title, model, visible, loading } = useForm<FormModel>({
  options: {
    baseApi: '/dictionary',
    model: initModel
  }
})

const open = async (id?: string) => {
  await pageLoader.open(id)
}

const addItem = () => {
  validateChildren()
  model?.value?.children && model.value.children.push(_.cloneDeep(initModel))
}

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

defineExpose({
  open
})
</script>
<template>
  <el-dialog v-model="visible" :width="width" :title="title">
    <el-form v-if="model" :model="model" label-position="top">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="名称" :label-width="formLabelWidth">
            <el-input v-model="model.itemName" autocomplete="off" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="编码" :label-width="formLabelWidth">
            <el-input v-model="model.itemValue" autocomplete="off" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-table v-if="model?.children" :data="model.children" style="width: 100%">
        <el-table-column label="名称" width="200">
          <template #default="scope">
            <el-input v-model="scope.row.itemName" place="条目编码" />
          </template>
        </el-table-column>
        <el-table-column label="编码" width="200">
          <template #default="scope">
            <el-input v-model="scope.row.itemValue" place="条目编码" />
          </template>
        </el-table-column>
        <el-table-column label="颜色" width="200">
          <template #default="scope">
            <el-input v-model="scope.row.color" place="颜色" />
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
        <el-button type="primary" @click="visible = false">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>
