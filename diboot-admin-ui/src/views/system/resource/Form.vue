<script setup lang="ts">
import elementResizeDetectorMaker from 'element-resize-detector'
import type { FormInstance } from 'element-plus'
import type { Resource } from './type'
import { Plus, Refresh, InfoFilled } from '@element-plus/icons-vue'
import RouteSelect from './components/RouteSelect.vue'
import PermissionSelect from './components/PermissionSelect.vue'
import { checkValue } from '@/utils/validate-form'

// 监听客户端宽度
const clientWidth = ref(window.innerWidth)
const erd = elementResizeDetectorMaker()
erd.listenTo(document.body, () => (clientWidth.value = document.body.clientWidth))
onBeforeUnmount(() => erd.uninstall(document.body))

const baseApi = '/iam/resource'

const props = defineProps<{ formValue?: Resource }>()

const model = ref<Resource>()

watch(
  () => props.formValue,
  value => {
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
  displayName: '新按钮权限',
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
</script>

<template>
  <div v-loading="submitting" class="form-container">
    <el-empty v-if="!model" description="选择左侧菜单后操作" style="flex: 1" />
    <el-scrollbar v-show="model" style="flex: 1">
      <el-row :gutter="5" style="width: 100%">
        <el-col :md="24" :lg="10">
          <div style="margin: 8px; zoom: 1.1">菜单配置</div>
          <el-scrollbar :style="clientWidth >= 1200 ? { height: 'calc(100vh - 168px)' } : {}">
            <el-form v-if="model" ref="formRef" :model="model" label-width="90px" style="margin-right: 8px">
              <el-form-item label="上级目录" prop="parentId">
                <el-input :model-value="model.parentId === '0' ? '顶级目录' : model.parentDisplayName" disabled />
              </el-form-item>
              <el-form-item label="菜单分类" prop="displayType">
                <el-radio-group v-model="model.displayType">
                  <el-radio-button label="CATALOGUE">目录</el-radio-button>
                  <el-radio-button label="MENU">菜单</el-radio-button>
                  <el-radio-button label="OUTSIDE_URL">外链</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item
                label="菜单名称"
                prop="displayName"
                :rules="{ required: true, message: '不能为空', trigger: 'blur' }"
              >
                <el-input v-model="model.displayName" placeholder="请输入菜单名称" clearable>
                  <template #append>
                    <i18n-selector v-model="model.displayNameI18n" />
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item label="菜单图标">
                <icon-select v-model="model.routeMeta.icon" />
              </el-form-item>
              <el-form-item
                label="路由路径"
                prop="routePath"
                :rules="{ required: true, message: '不能为空', trigger: 'blur' }"
              >
                <el-input v-model="model.routePath" placeholder="请输入路由地址（例：route-path）" clearable />
              </el-form-item>
              <el-form-item
                label="路由名称"
                prop="resourceCode"
                :rules="[
                  { required: true, message: '不能为空', trigger: 'blur' },
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
                  placeholder="请输入路由名称（例：RouteName）"
                  clearable
                />
              </el-form-item>
              <el-form-item v-if="model.displayType === 'CATALOGUE'" label="重定向">
                <el-input v-model="model.routeMeta.redirectPath" placeholder="请输入重定向" clearable />
              </el-form-item>
              <el-form-item
                v-if="model.displayType === 'OUTSIDE_URL'"
                label="外部链接"
                prop="routeMeta.url"
                :rules="{ required: true, message: '不能为空', trigger: 'blur' }"
              >
                <el-input v-model="model.routeMeta.url" placeholder="请输入外部链接" clearable>
                  <template #suffix>
                    <el-checkbox v-model="model.routeMeta.iframe" label="iframe" />
                  </template>
                </el-input>
              </el-form-item>
              <el-form-item v-if="model.displayType === 'MENU' && moduleList.length" label="应用模块">
                <el-select v-model="model.appModule" placeholder="应用模块" clearable>
                  <el-option v-for="item in moduleList" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
              <el-form-item v-if="model.displayType === 'MENU'" label="菜单权限">
                <el-select
                  v-model="model.permissionCodes"
                  multiple
                  popper-class="hide"
                  placeholder="点击聚焦后在权限列表中选择"
                  @focus="openPermissionConfig(model)"
                >
                  <el-option v-for="item in model.permissionCodes" :key="item" :label="item" :value="item" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <template #label>
                  <div style="display: flex; align-items: center; justify-content: end">
                    <span>其他配置</span>
                    <el-tooltip effect="dark" placement="top-start">
                      <template #content>
                        可用：控制菜单是否生效；<br />
                        隐藏：隐藏时菜单栏不会显示，但地址可以访问；<br />
                        缓存：页面开启keepAlive，缓存当前页面；<br />
                        <!-- 忽略认证：当前页面访问不需要权限认证。<br /> -->
                      </template>
                      <el-icon>
                        <InfoFilled />
                      </el-icon>
                    </el-tooltip>
                  </div>
                </template>
                <el-checkbox v-model="model.status" true-label="A" false-label="I" label="可用" />
                <el-checkbox v-model="model.routeMeta.hidden" label="隐藏" />
                <el-checkbox v-model="model.routeMeta.keepAlive" label="缓存" />
                <!-- <el-checkbox v-model="model.routeMeta.ignoreAuth" label="忽略认证" /> -->
              </el-form-item>

              <div v-show="model.displayType === 'MENU'">
                <el-divider style="margin: 12px 0" />
                <div style="display: flex; justify-content: space-between; margin: 0 12px">
                  <span>按钮权限配置</span>
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
                      <el-descriptions-item label="按钮权限编码">
                        <el-row type="flex" align="middle" :gutter="16">
                          <el-col :span="16">
                            <el-input
                              v-if="permission._customCode"
                              v-model="permission.resourceCode"
                              placeholder="请输入按钮权限编码"
                              @input="(value: string) => changeBtnResourceCode(permission, value)"
                            />
                            <el-select
                              v-else
                              v-model="permission.resourceCode"
                              filterable
                              allow-create
                              placeholder="请选取当前按钮权限编码"
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
                              输入/选择 切换
                            </el-button>
                          </el-col>
                        </el-row>
                      </el-descriptions-item>
                      <el-descriptions-item label="按钮权限名称">
                        <el-input
                          v-model="permission.displayName"
                          placeholder="请输入按钮权限名称"
                          @input="(value: string) => changeBtnPermissionName(permission, value)"
                        />
                      </el-descriptions-item>
                      <el-descriptions-item label="按钮权限接口">
                        <el-select
                          v-model="permission.permissionCodes"
                          multiple
                          popper-class="hide"
                          placeholder="点击聚焦后在权限列表中选择"
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
      <el-button size="default" type="primary" @click="model && submit(model, formRef)">保存</el-button>
      <el-button size="default" @click="resetForm()">重置</el-button>
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
