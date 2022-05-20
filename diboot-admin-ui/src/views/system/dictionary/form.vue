<script setup lang="ts" name="DictionaryForm">
import { defineEmits } from 'vue'
import useForm from '@/hooks/form'

interface FormModel {
  id?: string
  itemName: string
  itemValue: string
  description?: string
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

const formLabelWidth = '140px'
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

defineExpose({
  open
})
</script>
<template>
  <el-dialog v-model="visible" :width="width" :title="title">
    <el-form :model="model">
      <el-form-item label="名称" :label-width="formLabelWidth">
        <el-input v-model="model.itemName" autocomplete="off" />
      </el-form-item>
      <el-form-item label="名称" :label-width="formLabelWidth">
        <el-input v-model="model.itemValue" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="visible = false">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>
