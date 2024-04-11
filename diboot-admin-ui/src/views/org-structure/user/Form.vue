<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { UserModel, AccountInfo } from './type'
import UserPositionTableForm from '../position/UserPositionTableForm.vue'
import { checkValue } from '@/utils/validate-form'
import type { UserPosition } from '@/views/org-structure/position/type'

const baseApi = '/iam/user'

const { loadData, loading, model } = useDetail<
  UserModel & {
    roleIdList?: string[]
    userPositionList?: UserPosition[]
    isSysAccount?: boolean
    hidePassword?: boolean
  }
>(baseApi, { roleIdList: [] })

const { initRelatedData, relatedData } = useOption({
  dict: ['GENDER', 'ACCOUNT_STATUS'],
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

const oldUsername = ref<string>()

const switchType = (type?: boolean | number | string) => {
  if (type) {
    model.value.username = oldUsername.value
    return
  }
  oldUsername.value = model.value.username
  model.value.username = undefined
}

const loadAccountInfo = async (type: string, id?: string) => {
  type === 'authAccount' && (oldUsername.value = undefined)
  if (id != null) {
    const res = await api.get<AccountInfo>(`${baseApi}/account/${id}`)
    if (res.code === 0 && res.data) {
      return res.data[type as keyof AccountInfo]
    }
  }
}

defineExpose({
  open: async (id?: string) => {
    title.value = id ? '更新用户信息' : '新建用户'
    visible.value = true
    await loadData(id)
    if (model.value.roleList) model.value.roleIdList = model.value.roleList.map(e => e.id as string)
    model.value.username = await loadAccountInfo('authAccount', id)
    // 判定是否属于系统用户
    model.value.isSysAccount = id ? !!model.value.username : true
    if (model.value.isSysAccount) model.value.hidePassword = true
    // 加载树结构数据
    await initRelatedData()
    // 新建时状态默认在职
    !id && (model.value.status = 'A')
  }
})
// 表单
const formRef = ref<FormInstance>()

watch(visible, value => {
  if (!value) {
    formRef.value?.resetFields()
    userPositionTableForm.value?.clearDataList()
  }
})

const emit = defineEmits<{
  (e: 'complete', id?: string): void
}>()

const userPositionTableForm = ref()
const { submitting, submit } = useForm({
  baseApi,
  async afterValidate() {
    await userPositionTableForm.value?.validate()
  },
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})

const checkUsernameDuplicate = checkValue(`${baseApi}/check-username-duplicate`, 'username', () => model.value?.id)
const checkUserNumDuplicate = checkValue(`${baseApi}/check-user-num-duplicate`, 'userNum', () => model.value?.id)

const rules: FormRules = {
  orgId: { required: true, message: '不能为空', whitespace: true },
  username: [
    { required: true, message: '不能为空', whitespace: true },
    { validator: checkUsernameDuplicate, trigger: 'blur' }
  ],
  accountStatus: { required: true, message: '不能为空', whitespace: true },
  realname: { required: true, message: '不能为空', whitespace: true },
  userNum: [
    { required: true, message: '不能为空', whitespace: true },
    { validator: checkUserNumDuplicate, trigger: 'blur' }
  ],
  gender: { required: true, message: '不能为空', whitespace: true },
  status: { required: true, message: '不能为空', whitespace: true },
  email: {
    type: 'email',
    message: '请输入正确的邮箱地址',
    trigger: ['blur', 'change']
  },
  mobilePhone: { pattern: /^1[0-9][0-9]\d{8}$/, message: '请输入正确的手机号', trigger: ['blur', 'change'] }
}
</script>

<template>
  <el-dialog v-model="visible" :title="title" :width="720">
    <el-form ref="formRef" v-loading="loading" :model="model" :rules="rules" label-width="80px">
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item :required="true" prop="isSysAccount" label="用户类型">
            <el-radio-group v-model="model.isSysAccount" @change="switchType">
              <el-radio-button :label="false">普通用户</el-radio-button>
              <el-radio-button :label="true">系统用户</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="orgId" label="所属部门">
            <el-tree-select
              v-model="model.orgId"
              placeholder="请选择部门"
              class="tree-selector"
              :data="relatedData.orgTree"
              :default-expand-all="true"
              :check-strictly="true"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="realname" label="姓名">
            <el-input v-model="model.realname" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="userNum" label="员工编号">
            <el-input v-model="model.userNum" placeholder="请输入员工编号" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="model.isSysAccount" :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="model.username" placeholder="请输入用户名" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item
            prop="password"
            label="密码"
            :rules="
              model.hidePassword && model.id && model.isSysAccount
                ? []
                : { required: true, message: '不能为空', whitespace: true }
            "
            @click="model.hidePassword = false"
          >
            <el-button v-if="model.hidePassword && model.id && model.isSysAccount">修改密码</el-button>
            <el-input v-else v-model="model.password" placeholder="请输入密码" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col v-if="model.isSysAccount" :md="12" :sm="24">
          <el-form-item prop="roleIdList" label="角色" required>
            <di-selector
              v-model="model.roleIdList"
              multiple
              :list="{
                baseApi: '/iam/role',
                searchArea: {
                  propList: [
                    { prop: 'name', label: '名称', type: 'input' },
                    { prop: 'code', label: '编码', type: 'input' }
                  ]
                },
                columns: [
                  { prop: 'name', label: '名称' },
                  { prop: 'code', label: '编号' },
                  { prop: 'createTime', label: '创建时间' }
                ]
              }"
              data-type="IamRole"
              placeholder="选择角色"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="model.isSysAccount" :md="12" :sm="24">
          <el-form-item prop="accountStatus" label="账号状态">
            <el-select v-model="model.accountStatus" placeholder="请选择账号状态">
              <el-option
                v-for="item in relatedData.accountStatusOptions"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>
        </el-col>

        <el-col :md="12" :sm="24">
          <el-form-item prop="gender" label="性别">
            <el-radio-group v-model="model.gender">
              <el-radio
                v-for="item in relatedData.genderOptions"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              />
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="status" label="状态">
            <el-radio-group v-model="model.status">
              <el-radio label="A">在职</el-radio>
              <el-radio label="I">离职</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="birthday" label="生日">
            <el-date-picker v-model="model.birthday" value-format="YYYY-MM-DD" type="date" placeholder="请选择生日" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="mobilePhone" label="电话">
            <el-input v-model="model.mobilePhone" placeholder="请输入电话" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="model.email" placeholder="请输入邮箱" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="sortId" label="排序号">
            <el-input v-model="model.sortId" type="number" placeholder="排序号" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <UserPositionTableForm
      ref="userPositionTableForm"
      v-model="model.userPositionList"
      :org-tree="relatedData.orgTree"
      :user-id="model.id"
      :org-id="model.orgId"
    />
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="submitting" @click="submit(model, formRef)">保存</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.variable-tag {
  margin: 0 5px 5px 0;
  cursor: pointer;
}
</style>
