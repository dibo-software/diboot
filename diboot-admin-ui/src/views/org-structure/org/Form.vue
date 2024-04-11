<script setup lang="ts">
import type { OrgModel } from './type'
import type { FormInstance, FormRules } from 'element-plus'
import { checkValue } from '@/utils/validate-form'
import type { Select } from '@/components/di/type'

const baseApi = '/iam/org'

const { loadData, loading, model } = useDetail<OrgModel>(baseApi)

const { initRelatedData, relatedData } = useOption({
  load: {
    orgTree: {
      type: 'IamOrg',
      label: 'name',
      parent: 'parentId',
      lazyChild: false
    }
  }
})

const title = ref('')
const visible = ref(false)

const props = defineProps<{ parentId?: string; siblingsNumber?: number }>()

// 设置表单初始值
const setFormInitialValue = () => {
  nextTick(() => {
    model.value.parentId = props.parentId === '0' ? '' : props.parentId
    model.value.type = 'DEPT'
    model.value.sortId = props.siblingsNumber ? `${props.siblingsNumber + 1}` : '1'
  })
}

const open = async (id?: string) => {
  title.value = id ? '更新' : '新建'
  // 当新建模式时，设置系列初始值
  if (!id) {
    setFormInitialValue()
  }

  visible.value = true
  // 加载表单数据与树结构数据
  await loadData(id)
  await initRelatedData()
}

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) formRef.value?.resetFields()
})

//确认并继续添加后刷新排序值
watch(
  () => props.siblingsNumber,
  value => {
    if (!model.value.id) {
      model.value.sortId = value ? `${value + 1}` : '1'
    }
  }
)

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
      setFormInitialValue()
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
  parentId: { required: true, message: '不能为空', whitespace: true },
  name: { required: true, message: '不能为空', whitespace: true },
  code: [
    { required: true, message: '不能为空', whitespace: true },
    { validator: checkCodeDuplicate, trigger: 'blur' }
  ]
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
              :data="relatedData.orgTree"
              :default-expand-all="true"
              :check-strictly="true"
            />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="type" label="类型">
            <el-radio-group v-model="model.type">
              <el-radio label="COMP">公司</el-radio>
              <el-radio label="DEPT">部门</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="name" label="名称">
            <el-input v-model="model.name" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="code" label="编码">
            <el-input v-model="model.code" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="managerId" label="负责人">
            <di-selector
              v-model="model.managerId"
              :tree="{ type: 'IamOrg', label: 'name', parent: 'parentId', parentPath: 'parentIdsPath' }"
              :list="{
                baseApi: '/iam/user',
                relatedKey: 'orgId',
                searchArea: {
                  propList: [
                    { prop: 'realname', label: '姓名', type: 'input' },
                    { prop: 'userNum', label: '编号', type: 'input' },
                    { prop: 'gender', label: '性别', type: 'select', loader: 'GENDER' } as Select
                  ]
                },
                columns: [
                  { prop: 'realname', label: '姓名' },
                  { prop: 'userNum', label: '编号' },
                  { prop: 'genderLabel', label: '性别' },
                  { prop: 'email', label: '邮箱' }
                ]
              }"
              data-type="IamUser"
              placeholder="选择负责人"
            />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="sortId" label="排序">
            <el-input v-model="model.sortId" :min="1" type="number" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="orgComment" label="备注">
        <el-input v-model="model.orgComment" :rows="2" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button v-if="!model.id" type="primary" :loading="submitting" @click="beforeSubmit(true)"
        >保存并继续
      </el-button>
      <el-button type="primary" :loading="submitting" @click="beforeSubmit(false)">保存</el-button>
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
