<script setup lang="ts" name="PositionForm">
import { FormInstance, FormRules } from 'element-plus'
import type { Position } from './type'
import { defineEmits } from 'vue'

const baseApi = '/position'

const { loadData, loading, model } = useDetailDefault<Position>(baseApi)
const title = ref('')
const visible = ref(false)
defineExpose({
  open: (id?: string) => {
    title.value = id ? '更新' : '新建'
    loadData(id)
    loadAttachMore()
    visible.value = true
  }
})

// 加载附加信息
const more = ref<Record<string, any>>({})
const loadAttachMore = async () => {
  const res = await api.get<Record<string, any>>('/position/attachMore')
  if (res.code === 0 && res.data) {
    more.value = res.data
  }
}

// 表单
const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) formRef.value?.resetFields()
})

const emit = defineEmits<{
  (e: 'complete', id?: string): void
}>()

const { confirmSubmit, submit } = useFormDefault({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const onGradeValueChanged = (val: string) => {
  console.log('val', val)
  const grade = more.value.positionGradeOptions.find((item: any) => item.value === val)
  if (grade !== undefined) {
    model.value.gradeName = grade.label
  }
}

const rules: FormRules = {
  name: { required: true, message: '不能为空', whitespace: true },
  code: { required: true, message: '不能为空', whitespace: true }
}
</script>

<template>
  <el-dialog v-model="visible" :width="520" :title="title">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-form-item prop="name" label="名称">
        <el-input v-model="model.name" placeholder="请输入名称" />
      </el-form-item>
      <el-form-item prop="code" label="编码">
        <el-input v-model="model.code" placeholder="请输入编码" />
      </el-form-item>
      <el-form-item prop="gradeValue" label="职级">
        <el-select v-model="model.gradeValue" placeholder="请选择职级" @change="onGradeValueChanged">
          <template v-if="more.positionGradeOptions">
            <el-option
              v-for="item in more.positionGradeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </template>
        </el-select>
      </el-form-item>
      <el-form-item prop="gradeName" label="职级头衔">
        <el-input v-model="model.gradeName" placeholder="请输入职级头衔" />
      </el-form-item>
      <el-form-item prop="dataPermissionType" label="数据权限">
        <el-select v-model="model.dataPermissionType" placeholder="请选择数据权限">
          <template v-if="more.dataPermissionTypeOptions">
            <el-option
              v-for="item in more.dataPermissionTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </template>
        </el-select>
      </el-form-item>
      <el-form-item prop="isVirtual" label="虚拟岗位">
        <el-switch v-model="model.isVirtual" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="confirmSubmit" @click="submit(formRef, model)">提交</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
