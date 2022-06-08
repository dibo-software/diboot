<script setup lang="ts" name="RoleForm">
import type { FormInstance, FormRules } from 'element-plus'
import type { Role } from './type'
import type { ResourcePermission } from '@/views/system/resourcePermission/type'
import useTree from '@/views/system/resourcePermission/hooks/tree'

const baseApi = '/role'

const { loadData, loading, model } = useDetailDefault<Role>(baseApi)

const title = ref('')
const visible = ref(false)

// 权限树相关
const transformField = {
  label: 'displayName'
}
const { treeRef, treeDataList, selectedIdList, getTree, checkNode, flatTreeNodeClass } = useTree<ResourcePermission>({
  baseApi: '/resourcePermission',
  treeApi: '/list',
  transformField
})
const treeProps = {
  label: 'displayName',
  class: flatTreeNodeClass
}
getTree()

defineExpose({
  open: (id?: string) => {
    title.value = id ? '更新' : '新建'
    loadData(id).then(() => {
      // 设置选中权限
      if (model.value && model.value.permissionList) {
        selectedIdList.value = model.value.permissionList.map(item => item.id) as string[]
        treeRef.value?.setCheckedKeys(selectedIdList.value)
      }
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

const { confirmSubmit, submit } = useFormDefault({
  baseApi,
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const rules: FormRules = {
  name: { required: true, message: '不能为空', whitespace: true },
  code: { required: true, message: '不能为空', whitespace: true }
}
const handleCheckNode = (currentNode: ResourcePermission, data: { checkedKeys: string[] }) => {
  checkNode(currentNode, data)
  model.value.permissionList = selectedIdList.value.map(id => {
    return {
      id,
      parentId: '0'
    }
  })
}
</script>

<template>
  <el-dialog v-model="visible" :title="title">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-form-item prop="name" label="名称">
        <el-input v-model="model.name" />
      </el-form-item>
      <el-form-item prop="code" label="编码">
        <el-input v-model="model.code" />
      </el-form-item>
      <el-form-item prop="description" label="备注">
        <el-input v-model="model.description" type="textarea" />
      </el-form-item>
      <el-form-item prop="permissionList" label="角色授权">
        <el-scrollbar height="400px">
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
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="confirmSubmit" @click="submit(formRef, model)">提交</el-button>
    </template>
  </el-dialog>
</template>

<style scoped></style>
