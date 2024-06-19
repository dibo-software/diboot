<script setup lang="ts">
import elementResizeDetectorMaker from 'element-resize-detector'
import type { FormInstance } from 'element-plus'
import type { Resource } from './type'
import { Plus, Refresh, InfoFilled } from '@element-plus/icons-vue'
import RouteSelect from './components/RouteSelect.vue'
import PermissionSelect from './components/PermissionSelect.vue'
import { checkValue } from '@/utils/validate-form'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
// 监听客户端宽度
const clientWidth = ref(window.innerWidth)
const erd = elementResizeDetectorMaker()
erd.listenTo(document.body, () => (clientWidth.value = document.body.clientWidth))
onBeforeUnmount(() => erd.uninstall(document.body))

const baseApi = '/iam/resource'

const props = defineProps<{ formValue?: Resource }>()

const model = ref<Resource>()

const reloadFormItem = ref(false)
watch(
  () => props.formValue,
  value => {
    reloadFormItem.value = !reloadFormItem.value
    model.value = _.clone(value)
    configResource.value = model.value ?? {}
  }
)

const formRef = ref<FormInstance>()

const resetForm = () => {
  model.value = _.clone(props.formValue)
  configResource.value = model.value ?? {}
  formRef.value?.clearValidate()
}

const emit = defineEmits<{
  (e: 'complete', id: string): void
}>()

const { submitting, submit } = useForm({
  baseApi,
  successCallback(id) {
    emit('complete', id as string)
  }
})

const checkCodeDuplicate = checkValue(`${baseApi}/check-code-duplicate`, 'code', () => model.value?.id)

// 权限
const moduleList = ref<string[]>([])
const configResource = ref<Partial<Resource>>({})
const permissionSelectRef = ref<InstanceType<typeof PermissionSelect>>()

watch(configResource, () => permissionSelectRef.value?.relocation())

const openPermissionConfig = (permission?: Resource) => {
  if (!permission) return
  permission.permissionCodes ? permission.permissionCodes : (permission.permissionCodes = [])
  configResource.value = permission
}

// option hook
const { relatedData, initRelatedData } = useOption({ dict: 'RESOURCE_CODE' })
initRelatedData()

// 按钮权限配置
const NEW_PERMISSION_ITEM: Resource = {
  id: undefined,
  parentId: '',
  displayType: 'PERMISSION',
  displayName: i18n.t('resource.newButtonPermission'),
  resourceCode: '',
  permissionCodes: [],
  routeMeta: {}
}

const activeTab = ref('0')

const handleAddTab = () => {
  if (!model.value) return
  const permission = _.cloneDeep(NEW_PERMISSION_ITEM)
  const permissionList = model.value.permissionList ? model.value.permissionList : (model.value.permissionList = [])
  activeTab.value = permissionList.push(permission) - 1 + ''
  // 自动补全编码选项
  if (relatedData && relatedData.resourceCodeOptions) {
    const resourceCodes = permissionList.map(e => e.resourceCode)
    const validOption = relatedData.resourceCodeOptions.find(item => {
      return !resourceCodes.includes(item.value)
    })
    if (validOption) {
      permission.resourceCode = validOption.value
      permission.displayName = validOption.label
    }
  }
}

const handleRemoveTab = (index: string | number) => {
  model.value?.permissionList?.splice(parseInt(`${index}`), 1)
}

const handleChangeTab = (index: string | number) => {
  const permissionList = model.value?.permissionList ?? []
  configResource.value = permissionList[parseInt(`${index}`)]
}

const changeBtnResourceCode = (permission: Resource, code: string) => {
  const displayName = relatedData.resourceCodeOptions?.find(e => e.value === code)?.label
  if (displayName) permission.displayName = displayName
}
const changeBtnPermissionName = (permission: Resource, name: string) => {
  const resourceCode = relatedData.resourceCodeOptions?.find(e => e.label === name)?.value
  if (resourceCode) permission.resourceCode = resourceCode
}

const toggleBtnResourceCodeSelect = (permission: Resource) => {
  permission._customCode = !permission._customCode
  permission.resourceCode = ''
  permission.displayName = ''
}
const enableI18n = import.meta.env.VITE_APP_ENABLE_I18N === 'true'
</script>

<template>
  <div v-loading="submitting" class="form-container">
    <el-empty v-if="!model" :description="$t('resource.selectMenuAndOperate')" style="flex: 1" />
    <el-scrollbar v-show="model" style="flex: 1">
      <el-row :gutter="5" style="width: 100%">
        <el-col :md="24" :lg="10">
          <div style="margin: 8px; zoom: 1.1">{{ $t('resource.menuConfig') }}</div>
          <el-scrollbar :style="clientWidth >= 1200 ? { height: 'calc(100vh - 168px)' } : {}">
            <el-form
              v-if="model"
              ref="formRef"
              :model="model"
              :label-width="$i18n.locale === 'en' ? '150px' : '90px'"
              style="margin-right: 8px"
            >
              <el-form-item :label="$t('resource.parentId')" prop="parentId">
                <el-input
                  :model-value="model.parentId === '0' ? $t('resource.parentId0') : model.parentDisplayName"
                  disabled
                />
              </el-form-item>
              <el-form-item :label="$t('resource.displayType')" prop="displayType">
                <el-radio-group v-model="model.displayType">
                  <el-radio-button label="CATALOGUE">{{ $t('resource.displayTypeOptions.catalogue') }}</el-radio-button>
                  <el-radio-button label="MENU">{{ $t('resource.displayTypeOptions.menu') }}</el-radio-button>
                  <el-radio-button label="OUTSIDE_URL">{{
                    $t('resource.displayTypeOptions.outsideUrl')
                  }}</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item
                :label="$t('resource.displayName')"
                prop="displayName"
                :rules="{ required: true, message: i18n.t('rules.notnull'), trigger: 'blur' }"
              >
                <el-input v-model="model.displayName" :placeholder="$t('resource.placeholder.displayName')" clearable>
                  <template v-if="enableI18n" #append>
                    <i18n-selector v-model="model.displayNameI18n" />
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item :label="$t('resource.routeMetaIcon')">
                <icon-select v-model="model.routeMeta.icon" />
              </el-form-item>
              <el-form-item
                :label="$t('resource.routePath')"
                prop="routePath"
                :rules="{ required: true, message: i18n.t('rules.notnull'), trigger: 'blur' }"
              >
                <el-input v-model="model.routePath" :placeholder="$t('resource.placeholder.routePath')" clearable />
              </el-form-item>
              <el-form-item
                :label="$t('resource.resourceCode')"
                prop="resourceCode"
                :rules="[
                  { required: true, message: i18n.t('rules.notnull'), trigger: 'blur' },
                  { validator: checkCodeDuplicate, trigger: 'blur' }
                ]"
              >
                <route-select
                  v-show="model.displayType === 'MENU'"
                  v-model="model.resourceCode"
                  v-model:component-path="model.routeMeta.componentPath"
                  @change="formRef?.validateField('resourceCode')"
                />
                <el-input
                  v-show="model.displayType !== 'MENU'"
                  v-model="model.resourceCode"
                  :placeholder="$t('resource.placeholder.resourceCode')"
                  clearable
                />
              </el-form-item>
              <el-form-item v-if="model.displayType === 'CATALOGUE'" :label="$t('resource.redirectPath')">
                <el-input
                  v-model="model.routeMeta.redirectPath"
                  :placeholder="$t('resource.placeholder.redirectPath')"
                  clearable
                />
              </el-form-item>
              <el-form-item
                v-if="model.displayType === 'OUTSIDE_URL'"
                :label="$t('resource.routeMetaUrl')"
                prop="routeMeta.url"
                :rules="{ required: true, message: i18n.t('rules.notnull'), trigger: 'blur' }"
              >
                <el-input
                  v-model="model.routeMeta.url"
                  :placeholder="$t('resource.placeholder.routeMetaUrl')"
                  clearable
                >
                  <template #suffix>
                    <el-checkbox v-model="model.routeMeta.iframe" label="iframe" />
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item v-if="model.displayType === 'MENU' && moduleList.length" :label="$t('resource.appModule')">
                <el-select v-model="model.appModule" :placeholder="$t('resource.appModule')" clearable>
                  <el-option v-for="item in moduleList" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="model.displayType === 'MENU'" :label="$t('resource.permissionCodes')">
                <el-select
                  v-model="model.permissionCodes"
                  multiple
                  popper-class="hide"
                  :placeholder="$t('resource.placeholder.permissionCodes')"
                  @focus="openPermissionConfig(model)"
                >
                  <el-option v-for="item in model.permissionCodes" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <template #label>
                  <div style="display: flex; align-items: center; justify-content: end">
                    <span>{{ $t('resource.otherConfig') }}</span>
                    <el-tooltip effect="dark" placement="top-start">
                      <template #content>
                        {{ $t('resource.otherConfigDesc.status') }}<br />
                        {{ $t('resource.otherConfigDesc.hidden') }}<br />
                        {{ $t('resource.otherConfigDesc.keepAlive') }}<br />
                        <!--{{$t('resource.otherConfigDesc.ignoreAuth')}}<br /> -->
                      </template>
                      <el-icon>
                        <InfoFilled />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>
                <el-checkbox
                  :key="reloadFormItem"
                  v-model="model.status"
                  true-label="A"
                  false-label="I"
                  :label="$t('resource.status')"
                />
                <el-checkbox :key="reloadFormItem" v-model="model.routeMeta.hidden" :label="$t('resource.hidden')" />
                <el-checkbox
                  :key="reloadFormItem"
                  v-model="model.routeMeta.keepAlive"
                  :label="$t('resource.keepAlive')"
                />
                <!-- <el-checkbox v-model="model.routeMeta.ignoreAuth" :label="$t('resource.ignoreAuth')" /> -->
              </el-form-item>

              <div v-show="model.displayType === 'MENU'">
                <el-divider style="margin: 12px 0" />
                <div style="display: flex; justify-content: space-between; margin: 0 12px">
                  <span> {{ $t('resource.buttonPermission') }}</span>
                  <el-button :icon="Plus" circle type="success" size="small" @click="handleAddTab" />
                </div>
                <el-tabs
                  v-if="model.permissionList?.length"
                  v-model="activeTab"
                  type="card"
                  closable
                  @tab-remove="handleRemoveTab"
                  @tab-change="handleChangeTab"
                >
                  <el-tab-pane
                    v-for="(permission, index) in model.permissionList"
                    :key="`tab_${index}`"
                    :label="permission.displayName"
                    :name="`${index}`"
                  >
                    <el-descriptions :column="1" style="margin-left: 8px">
                      <el-descriptions-item :label="$t('resource.buttonPermissionConfig.resourceCode')">
                        <el-row type="flex" align="middle" :gutter="16">
                          <el-col :span="16">
                            <el-input
                              v-if="permission._customCode"
                              v-model="permission.resourceCode"
                              :placeholder="$t('resource.placeholder.buttonPermissionConfig._customCode')"
                              @input="(value: string) => changeBtnResourceCode(permission, value)"
                            />
                            <el-select
                              v-else
                              v-model="permission.resourceCode"
                              filterable
                              allow-create
                              :placeholder="$t('resource.placeholder.buttonPermissionConfig.resourceCode')"
                              @change="(value: string) => changeBtnResourceCode(permission, value)"
                            >
                              <el-option
                                v-for="(item, i) in relatedData.resourceCodeOptions"
                                :key="`frontend-code_${i}`"
                                :label="`${item.label}[${item.value}]`"
                                :value="item.value"
                              />
                            </el-select>
                          </el-col>
                          <el-col :span="8">
                            <el-button
                              type="primary"
                              :icon="Refresh"
                              size="small"
                              @click="toggleBtnResourceCodeSelect(permission)"
                            >
                              {{ $t('resource.switchButton') }}
                            </el-button>
                          </el-col>
                        </el-row>
                      </el-descriptions-item>
                      <el-descriptions-item :label="$t('resource.buttonPermissionConfig.displayName')">
                        <el-input
                          v-model="permission.displayName"
                          :placeholder="$t('resource.placeholder.buttonPermissionConfig.displayName')"
                          @input="(value: string) => changeBtnPermissionName(permission, value)"
                        >
                          <template v-if="enableI18n" #append>
                            <i18n-selector v-model="permission.displayNameI18n" />
                          </template>
                        </el-input>
                      </el-descriptions-item>
                      <el-descriptions-item :label="$t('resource.buttonPermissionConfig.permissionCodes')">
                        <el-select
                          v-model="permission.permissionCodes"
                          multiple
                          popper-class="hide"
                          :placeholder="$t('resource.placeholder.permissionCodes')"
                          @focus="openPermissionConfig(permission)"
                        >
                          <el-option
                            v-for="item in permission.permissionCodes"
                            :key="item"
                            :label="item"
                            :value="item"
                          />
                        </el-select>
                      </el-descriptions-item>
                    </el-descriptions>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </el-form>
          </el-scrollbar>
        </el-col>
        <el-col :md="24" :lg="14">
          <div :style="clientWidth >= 1200 ? { height: 'calc(100vh - 126px)' } : {}">
            <permission-select
              ref="permissionSelectRef"
              v-model:permission-codes="configResource.permissionCodes"
              :app-module="model?.appModule"
              :menu-type="model?.displayType"
              :menu-code="model?.resourceCode"
              :display-name="configResource.displayName"
              @module-list="value => (moduleList = value)"
            />
          </div>
        </el-col>
      </el-row>
    </el-scrollbar>
    <div v-show="model" class="form-button">
      <el-button size="default" type="primary" @click="model && submit(model, formRef)">{{
        $t('button.save')
      }}</el-button>
      <el-button size="default" @click="resetForm()">{{ $t('operation.reset') }}</el-button>
    </div>
  </div>
</template>

<style scoped lang="scss">
.form-container {
  display: flex;
  flex-direction: column;

  .form-button {
    height: 38px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-top: 1px solid var(--el-border-color-lighter);
  }
}
</style>
