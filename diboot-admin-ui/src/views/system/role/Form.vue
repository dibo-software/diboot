<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { Role } from './type'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
import CommonTree from './CommonTree.vue'

const i18n = useI18n()
const baseApi = '/iam/role'

const { loadData, loading, model } = useDetail<Role & { permissionIdList?: string[] }>(baseApi)

const title = ref('')
const visible = ref(false)
const treeRef = ref()
const activeName = ref('PC')

// 新建完是否清空表单继续填写
const isContinueAdd = ref(false)

defineExpose({
  open: (id?: string) => {
    title.value = id ? i18n.t('title.update') : i18n.t('title.create')
    activeName.value = 'PC'
    loadData(id).then(() => {
      // 设置选中权限
      model.value.permissionIdList = (model.value.permissionList?.map(item => item.id) as string[]) ?? []
      treeRef.value?.setCheckedKeys(model.value.permissionIdList)
    })
    visible.value = true
  }
})

// 表单
const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) formRef.value?.resetFields()
})

watch(activeName, () => {
  setTimeout(() => {
    treeRef.value?.setCheckedKeys(model.value.permissionIdList)
  }, 0)
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

const getSelectedIdList = (idList: string[]) => {
  model.value.permissionIdList = idList
}
</script>

<template>
  <el-dialog v-model="visible" :title="title" top="10vh" draggable>
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '150px' : '80px'"
    >
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item prop="name" :label="$t('role.name')">
            <el-input v-model="model.name" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="code" :label="$t('role.code')">
            <el-input v-model="model.code" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item prop="description" :label="$t('role.description')">
        <el-input v-model="model.description" type="textarea" :rows="1" />
      </el-form-item>
      <el-form-item prop="permissionList" :label="$t('role.permissionList')">
        <el-radio-group v-model="activeName" style="margin-right: 20px" fill="#909399">
          <el-radio-button :label="$t('resource.main')" value="PC" />
          <el-radio-button :label="$t('resource.mobile.title')" value="Mobile" />
        </el-radio-group>
        <common-tree
          v-if="activeName === 'PC'"
          ref="treeRef"
          :init-query-param="{ appModule: 'PC' }"
          @get-selected-id-list="getSelectedIdList"
        />
        <common-tree
          v-if="activeName === 'Mobile'"
          ref="treeRef"
          :init-query-param="{ appModule: 'MOBILE' }"
          @get-selected-id-list="getSelectedIdList"
        />
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

<style scoped lang="scss">
.el-tree {
  :deep(.mobile) {
    color: var(--el-color-primary-dark-2);
  }
}
</style>
