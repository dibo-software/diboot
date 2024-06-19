<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { Role } from './type'
import type { Resource } from '@/views/system/resource/type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/iam/role'

const { loadData, loading, model } = useDetail<Role & { permissionIdList?: string[] }>(baseApi)

const title = ref('')
const visible = ref(false)

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

// 权限树相关
const transformField = {
  label: 'displayName'
}
const { treeRef, treeDataList, selectedIdList, getTree, checkNode, flatTreeNodeClass } = useTreeCrud<Resource>({
  baseApi: '/iam/resource',
  treeApi: '',
  transformField
})
const treeProps = {
  label: 'displayName',
  class: flatTreeNodeClass
}
getTree()

defineExpose({
  open: (id?: string) => {
    title.value = id ? i18n.t('title.update') : i18n.t('title.create')
    loadData(id).then(() => {
      // 设置选中权限
      selectedIdList.value = (model.value.permissionList?.map(item => item.id) as string[]) ?? []
      treeRef.value?.setCheckedKeys(selectedIdList.value)
    })
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

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = isContinueAdd.value
    if (isContinueAdd.value) {
      formRef.value?.resetFields()
      selectedIdList.value = []
      model.value.permissionIdList = []
      treeRef.value?.setCheckedKeys([])
    }
  }
})

// 保存之前判断是否确认并继续添加
const beforeSubmit = (value: boolean) => {
  isContinueAdd.value = value
  submit(model.value, formRef.value)
}

const checkCodeDuplicate = checkValue(`${baseApi}/check-code-duplicate`, 'code', () => model.value?.id)

const rules: FormRules = {
  name: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  code: [
    { required: true, message: i18n.t('rules.notnull'), whitespace: true },
    { validator: checkCodeDuplicate, trigger: 'blur' }
  ]
}
const handleCheckNode = (currentNode: Resource, data: { checkedKeys: string[] }) => {
  checkNode(currentNode, data)
  model.value.permissionIdList = selectedIdList.value
}
</script>

<template>
  <el-dialog v-model="visible" :title="title" top="10vh">
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '150px' : '80px'"
    >
      <el-form-item prop="name" :label="$t('role.name')">
        <el-input v-model="model.name" />
      </el-form-item>
      <el-form-item prop="code" :label="$t('role.code')">
        <el-input v-model="model.code" />
      </el-form-item>
      <el-form-item prop="description" :label="$t('role.description')">
        <el-input v-model="model.description" type="textarea" />
      </el-form-item>
      <el-form-item prop="permissionList" :label="$t('role.permissionList')">
        <el-scrollbar height="calc(80vh - 350px)">
          <el-tree
            ref="treeRef"
            style="width: 100%"
            :expand-on-click-node="false"
            :props="treeProps"
            :data="treeDataList"
            show-checkbox
            check-strictly
            node-key="id"
            default-expand-all
            @check="handleCheckNode"
          />
        </el-scrollbar>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button v-if="!model.id" type="primary" :loading="submitting" @click="beforeSubmit(true)"
        >{{ $t('button.continueAdd') }}
      </el-button>
      <el-button type="primary" :loading="submitting" @click="beforeSubmit(false)">{{ $t('button.save') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
