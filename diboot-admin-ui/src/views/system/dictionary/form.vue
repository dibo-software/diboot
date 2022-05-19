<script setup lang="ts">
import { defineEmits } from 'vue'

interface FormModel {
  id?: string
  itemName: string
  itemValue: string
  description?: string
  children?: FormModel[]
}
type Props = {
  title?: string
  type?: string
}
const props = withDefaults(defineProps<Props>(), {
  title: '数据编辑',
  type: 'modal'
})
const emit = defineEmits(['complete'])

const formLabelWidth = '140px'
const visible = ref(false)
const form = reactive<FormModel>({
  itemName: '',
  itemValue: '',
  description: '',
  children: []
})

class BaseFormLoader<T> {
  public options: FormOptions<T>
}

const open = async (id?: string) => void {}
</script>
<template>
  <el-dialog v-model="visible" :title="title">
    <el-form :model="form">
      <el-form-item label="Promotion name" :label-width="formLabelWidth">
        <el-input v-model="form.name" autocomplete="off" />
      </el-form-item>
      <el-form-item label="Zones" :label-width="formLabelWidth">
        <el-select v-model="form.region" placeholder="Please select a zone">
          <el-option label="Zone No.1" value="shanghai" />
          <el-option label="Zone No.2" value="beijing" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">Cancel</el-button>
        <el-button type="primary" @click="visible = false">Confirm</el-button>
      </span>
    </template>
  </el-dialog>
</template>
