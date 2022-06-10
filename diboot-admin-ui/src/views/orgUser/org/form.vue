<script setup lang="ts" name="OrgForm">
import type { OrgModel } from './type'
import type { FormInstance, FormRules } from 'element-plus'
import { defineEmits, defineProps } from 'vue'
import useTreeCrud from '@/hooks/tree_crud'

const baseApi = '/org'

const { loadData, loading, model } = useDetailDefault<OrgModel>(baseApi)
const {
  getTree,
  treeDataList,
  loading: treeLoading
} = useTreeCrud<OrgModel>({
  baseApi: '/org',
  treeApi: '/tree',
  transformField: { label: 'shortName' }
})

const title = ref('')
const visible = ref(false)

// 定义当前父节点入参
type Props = {
  parentId?: string
}
const props = withDefaults(defineProps<Props>(), {
  parentId: '0'
})

const open = async (id?: string) => {
  title.value = id ? '更新' : '新建'
  // 当新建模式时，设置系列初始值
  if (!id) {
    model.value['parentId'] = props.parentId
    model.value['type'] = 'DEPT'
  }
  visible.value = true
  // 加载表单数据与树结构数据
  await loadData(id)
  await getTree()
  // 添加顶级菜单时的显示文本
  const firstItem: OrgModel = {
    id: '0',
    parentId: '0',
    topOrgId: '0',
    name: '-无-',
    shortName: '-无-',
    type: 'COMP',
    code: 'NONE',
    managerId: '',
    depth: 0,
    createTime: ''
  }
  treeDataList.value?.unshift(firstItem)
}

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
    console.log('successCallback id', id)
    emit('complete', id)
    visible.value = false
  }
})

const rules: FormRules = {
  name: { required: true, message: '不能为空', whitespace: true },
  shortName: { required: true, message: '不能为空', whitespace: true },
  code: { required: true, message: '不能为空', whitespace: true }
}

defineExpose({ open })
</script>

<template>
  <el-dialog v-model="visible" :title="title">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="parentId" label="上级部门">
            <el-tree-select
              v-model="model.parentId"
              class="tree-selector"
              :data="treeDataList"
              :props="{ label: 'shortName', value: 'id' }"
              :default-expand-all="true"
              :check-strictly="true"
            />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="code" label="编码">
            <el-input v-model="model.code" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="name" label="全称">
            <el-input v-model="model.name" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="shortName" label="简称">
            <el-input v-model="model.shortName" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="type" label="类型">
            <el-select v-model="model.type">
              <el-option key="COMP" label="公司" value="COMP" />
              <el-option key="DEPT" label="部门" value="DEPT" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注">
        <el-input v-model="model.orgComment" :rows="2" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="confirmSubmit" @click="submit(formRef, model)">提交</el-button>
    </template>
  </el-dialog>
</template>
<style lang="scss" scoped>
.tree-selector {
  :deep(.el-tree-node__content) {
    height: 32px;
  }
}
</style>
