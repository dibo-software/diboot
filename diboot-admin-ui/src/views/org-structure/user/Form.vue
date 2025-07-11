<script setup lang="ts">
import type { FormInstance, FormRules } from 'element-plus'
import type { UserModel, AccountInfo } from './type'
import UserPositionTableForm from '../position/UserPositionTableForm.vue'
import { checkValue } from '@/utils/validate-form'
import type { UserPosition } from '@/views/org-structure/position/type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/iam/user'

const { loadData, loading, model } = useDetail<
  UserModel & {
    roleIdList?: string[]
    userPositionList?: UserPosition[]
    isSysAccount?: boolean
    hidePassword?: boolean
    primaryPosition: Partial<UserPosition>
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
    title.value = id ? i18n.t('title.update') : i18n.t('title.create')
    visible.value = true
    await loadData(id).then(() => {
      const index = model.value.userPositionList?.findIndex(e => e.isPrimaryPosition)
      if (index == null || index < 0) model.value.primaryPosition = {}
      else model.value.primaryPosition = model.value.userPositionList!.splice(index, 1)[0]
    })
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
    userPositionTableFormRef.value?.clearDataList()
  }
})

const emit = defineEmits<{
  (e: 'complete', id?: string): void
}>()

const userPositionTableFormRef = ref()
const { submitting, submit } = useForm({
  baseApi,
  async afterValidate() {
    await userPositionTableFormRef.value?.validate()
  },
  successCallback(id) {
    emit('complete', id)
    visible.value = false
  }
})
const visiblePosition = ref(false)

const onSubmit = () => {
  const data = _.cloneDeep(model.value)
  if (data.primaryPosition?.positionId) {
    if (!data.userPositionList) data.userPositionList = []
    const primaryPosition = data.primaryPosition
    if (!data.primaryPosition.id) primaryPosition.orgId = model.value.orgId
    data.userPositionList.splice(0, 0, primaryPosition as UserPosition)
  } else data.userPositionList = []
  submit(data, formRef.value)
}

const checkUsernameDuplicate = checkValue(`${baseApi}/check-username-duplicate`, 'username', () => model.value?.id)
const checkUserNumDuplicate = checkValue(`${baseApi}/check-user-num-duplicate`, 'userNum', () => model.value?.id)

const rules: FormRules = {
  orgId: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  username: [
    { required: true, message: i18n.t('rules.notnull'), whitespace: true },
    { validator: checkUsernameDuplicate, trigger: 'blur' }
  ],
  roleIdList: { required: true, message: i18n.t('rules.notnull') },
  accountStatus: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  realname: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  userNum: [
    { required: true, message: i18n.t('rules.notnull'), whitespace: true },
    { validator: checkUserNumDuplicate, trigger: 'blur' }
  ],
  gender: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  status: { required: true, message: i18n.t('rules.notnull'), whitespace: true },
  email: {
    type: 'email',
    message: i18n.t('user.rules.email'),
    trigger: ['blur', 'change']
  },
  mobilePhone: { pattern: /^1[0-9][0-9]\d{8}$/, message: i18n.t('user.rules.mobilePhone'), trigger: ['blur', 'change'] }
}
</script>

<template>
  <el-dialog v-model="visible" :title="title" :width="800" draggable>
    <el-form
      ref="formRef"
      v-loading="loading"
      :model="model"
      :rules="rules"
      :label-width="$i18n.locale === 'en' ? '120px' : '80px'"
    >
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item :required="true" prop="isSysAccount" :label="$t('user.type')">
            <el-radio-group v-model="model.isSysAccount" @change="switchType">
              <el-radio-button :value="false">{{ $t('user.general') }}</el-radio-button>
              <el-radio-button :value="true">{{ $t('user.system') }}</el-radio-button>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="orgId" :label="$t('user.orgId')">
            <el-tree-select
              v-model="model.orgId"
              :placeholder="$t('user.placeholder.orgId')"
              class="tree-selector"
              :data="relatedData.orgTree"
              filterable
              default-expand-all
              check-strictly
              clearable
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="userNum" :label="$t('user.userNum')">
            <el-input v-model="model.userNum" :placeholder="$t('user.placeholder.userNum')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="realname" :label="$t('user.realname')">
            <el-input v-model="model.realname" :placeholder="$t('user.placeholder.realname')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row v-if="model.isSysAccount" :gutter="18">
        <el-col :md="12" :sm="24">
          <el-form-item prop="username" :label="$t('user.username')">
            <el-input v-model="model.username" :placeholder="$t('user.placeholder.username')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item
            prop="password"
            :label="$t('user.password')"
            :rules="
              model.hidePassword && model.id && model.isSysAccount
                ? []
                : { required: true, message: i18n.t('rules.notnull'), whitespace: true }
            "
            @click="model.hidePassword = false"
          >
            <el-button v-if="model.hidePassword && model.id && model.isSysAccount">{{
              $t('user.modifyPassword')
            }}</el-button>
            <el-input v-else v-model="model.password" :placeholder="$t('user.placeholder.password')" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col v-if="model.isSysAccount" :md="12" :sm="24">
          <el-form-item prop="roleIdList" :label="$t('user.role')">
            <di-selector
              v-model="model.roleIdList"
              multiple
              :list="{
                baseApi: '/iam/role',
                searchArea: {
                  propList: [
                    { prop: 'name', label: $t('role.name'), type: 'input' },
                    { prop: 'code', label: $t('role.code'), type: 'input' }
                  ]
                },
                columns: [
                  { prop: 'name', label: $t('role.name') },
                  { prop: 'code', label: $t('role.code') },
                  { prop: 'createTime', label: $t('baseField.createTime') }
                ]
              }"
              data-type="IamRole"
              :placeholder="$t('user.placeholder.role')"
            />
          </el-form-item>
        </el-col>
        <el-col v-if="model.isSysAccount" :md="12" :sm="24">
          <el-form-item prop="accountStatus" :label="$t('user.accountStatus')">
            <el-select v-model="model.accountStatus" :placeholder="$t('user.placeholder.accountStatus')">
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
          <el-form-item prop="mobilePhone" :label="$t('position.label')">
            <div style="flex: 1">
              <di-selector
                v-if="model.primaryPosition"
                v-model="model.primaryPosition.positionId"
                :list="{
                  baseApi: '/iam/position',
                  searchArea: {
                    propList: [
                      { prop: 'name', label: $t('position.name'), type: 'input' },
                      { prop: 'code', label: $t('position.code'), type: 'input' }
                    ]
                  },
                  columns: [
                    { prop: 'name', label: $t('position.name') },
                    { prop: 'code', label: $t('position.code') },
                    { prop: 'gradeValue', label: $t('position.gradeValue') },
                    { prop: 'gradeName', label: $t('position.gradeNameAlias') },
                    { prop: 'createTime', label: $t('baseField.createTime') }
                  ]
                }"
                data-type="IamPosition"
                :placeholder="$t('position.placeholder.label')"
              />
            </div>
            <el-badge
              v-if="model.primaryPosition?.positionId"
              :value="model.userPositionList?.length"
              :show-zero="false"
              :offset="[0, 9]"
              color="green"
            >
              <el-button @click="visiblePosition = true">{{ $t('position.deputy') }}</el-button>
            </el-badge>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="gender" :label="$t('user.gender')">
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
          <el-form-item prop="status" :label="$t('user.status')">
            <el-radio-group v-model="model.status">
              <el-radio value="A">{{ $t('user.onJob') }}</el-radio>
              <el-radio value="I">{{ $t('user.dimission') }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="birthday" :label="$t('user.birthday')">
            <el-date-picker
              v-model="model.birthday"
              value-format="YYYY-MM-DD"
              type="date"
              :placeholder="$t('user.placeholder.birthday')"
            />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="mobilePhone" :label="$t('user.mobilePhone')">
            <el-input v-model="model.mobilePhone" :placeholder="$t('user.placeholder.mobilePhone')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="email" :label="$t('user.email')">
            <el-input v-model="model.email" :placeholder="$t('user.placeholder.email')" />
          </el-form-item>
        </el-col>
        <el-col :md="12" :sm="24">
          <el-form-item prop="sortId" :label="$t('user.sortId')">
            <el-input v-model="model.sortId" type="number" :placeholder="$t('user.placeholder.sortId')" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-dialog v-model="visiblePosition" :title="$t('position.deputy')" width="600" append-to-body>
      <UserPositionTableForm
        ref="userPositionTableFormRef"
        v-model="model.userPositionList"
        :org-tree="relatedData.orgTree"
        :user-id="model.id"
        :org-id="model.orgId"
      />
      <template #footer>
        <el-button @click="visiblePosition = false">{{ $t('button.close') }}</el-button>
      </template>
    </el-dialog>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.cancel') }}</el-button>
      <el-button type="primary" :loading="submitting" @click="onSubmit">
        {{ $t('button.save') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.variable-tag {
  margin: 0 5px 5px 0;
  cursor: pointer;
}
</style>
