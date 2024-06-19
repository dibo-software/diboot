<script lang="ts" setup>
import type { Resource } from '@/views/system/resource/type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const visible = ref(false)

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
const handleCheckNode = (currentNode: Resource, data: { checkedKeys: string[] }) => {
  checkNode(currentNode, data)
}
const baseApi = '/iam/tenant/resource'
const tenantId = ref()
const loadPermissionIdList = async () => {
  const res = await api.get(`${baseApi}/${tenantId.value}`)
  if (res.code === 0) {
    selectedIdList.value = res.data || []
    treeRef.value?.setCheckedKeys(selectedIdList.value)
  } else {
    throw new Error(res.msg)
  }
}
const submitting = ref(false)
const submit = async () => {
  submitting.value = true
  try {
    const res = await api.post(`${baseApi}/${tenantId.value}`, selectedIdList.value)
    if (res.code === 0) {
      ElMessage.success(i18n.t('tenantInfo.resourceForm.success'))
      visible.value = false
    } else {
      ElMessage.error(res.msg)
    }
  } finally {
    submitting.value = false
  }
}
defineExpose({
  open: (id: string) => {
    tenantId.value = id
    loadPermissionIdList()
    visible.value = true
  }
})
</script>
<template>
  <el-dialog v-model="visible" width="60%" :title="$t('tenantInfo.resourceForm.title')">
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
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="submit()"> 确定 </el-button>
    </template>
  </el-dialog>
</template>
