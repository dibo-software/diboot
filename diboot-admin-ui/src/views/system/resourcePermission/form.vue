<script lang="ts" setup>
import RouteSelect from './modules/RouteSelect.vue'
import PermissionCodeSelect from './modules/PermissionCodeSelect.vue'
import PermissionCodeList from './modules/PermissionCodeList.vue'
import { Plus, Refresh, InfoFilled } from '@element-plus/icons-vue'

import type { FormInstance, FormRules } from 'element-plus'
import type { ResourcePermission } from './type'
import useDisplayControl from './hooks/displayControl'
import usePermissionControl from './hooks/permissionControl'
import useScrollbarHeight from './hooks/scrollbarHeight'
import type { MenuType } from './hooks/displayControl'

// ======> 响应式数据
const empty = ref(true)

const formRef = ref<FormInstance>()

const rules = reactive<FormRules>({
  displayName: [
    {
      required: true,
      message: '请输入菜单名称',
      trigger: 'blur'
    }
  ]
})
const model = ref<Partial<ResourcePermission>>({
  routeMeta: {}
})
const submitLoading = ref(false)
// ======> 按钮权限相关
const NEW_PERMISSION_ITEM: ResourcePermission = {
  id: undefined,
  parentId: '',
  displayType: 'PERMISSION',
  displayName: '新按钮权限',
  resourceCode: '',
  permissionCodes: []
}
// ======> props
const props = defineProps<{ formValue: Partial<ResourcePermission> }>()
const boxHeight = inject<number>('boxHeight', 0)
// ======> 本地方法
// 切换菜单类型
const handleChangeDisplayType = (val: string | number | boolean) => {
  changeDisplayType(val as MenuType)
}
// 添加按钮权限
const handleAddTab = () => {
  addTab(_.cloneDeep(NEW_PERMISSION_ITEM))
}
// 切换tab
const handleChangeTab = (name: string | number) => {
  if (model.value.permissionList) {
    const permission = model.value.permissionList[parseInt(`${name}`, 10)]
    // 切换按钮权限tab时自动切换权限配置
    clickConfigPermission(permission)
  }
}

const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      submitLoading.value = true
      api
        .put(`/resourcePermission/${model.value.id}`, model.value)
        .then(res => {
          console.log(res)
        })
        .finally(() => {
          submitLoading.value = false
        })
    } else {
      console.log('error submit!', fields)
    }
  })
}
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
// compute
// 计算已经存在的tab权限码
const existPermissionCodes = computed(() => {
  if (!tabs.value) return []
  return tabs.value.map(item => item.resourceCode as string)
})
// ======> hook相关
// 滚动高度计算hook
const { height, computedFixedHeight } = useScrollbarHeight({
  boxHeight,
  fixedBoxSelectors: ['.form-space-container>.el-space__item:first-child', '.btn-fixed'],
  extraHeight: 20
})
// 菜单类型切换控制字段
const { displayFields, changeDisplayType } = useDisplayControl()

// 权限相关hooks
const {
  toggle,
  btnResourceCodeSelect,
  configPermissionTitle,
  configResourceCode,
  configPermissionCodes,
  loadingRestPermissions,
  restPermissions,
  initRestPermissions,
  initResourcePermissionCodeOptions,
  initReactiveData,
  changeBtnResourceCode,
  changeBtnPermissionName,
  toggleBtnResourceCodeSelect,
  clickConfigPermission,
  autoRefreshPermissionCode
} = usePermissionControl()
// 初始化后台权限
initRestPermissions('/resourcePermission/apiList')
// more hook
const { more, initMore } = useMoreDefault({ dict: 'RESOURCE_PERMISSION_CODE' })
initMore().then(() => {
  // 初始化权限选项
  initResourcePermissionCodeOptions(more.resourcePermissionCodeOptions)
})
// tab hooks => 按钮权限处理
const { activeTab, tabs, initTabs, removeTab, addTab } = useTabs<ResourcePermission>({
  beforeAddTab(tab) {
    // 自动补全编码选项
    if (more && more.resourcePermissionCodeOptions) {
      const validOption = more.resourcePermissionCodeOptions.find(item => {
        return !existPermissionCodes.value.includes(item.value)
      })
      if (validOption) {
        tab.resourceCode = validOption.value
        tab.displayName = validOption.label
      }
      configResourceCode.value = tab.resourceCode ?? ''
      configPermissionCodes.value = []
    }
  }
})
// 监听

watch(
  () => props.formValue,
  val => {
    empty.value = false
    model.value = val
    // 控制显示字段
    changeDisplayType(val.displayType as MenuType)
    // 初始化按钮权限tab
    initTabs(val.permissionList ?? [])
    // 初始化权限响应数据
    initReactiveData(val.permissionCodes ?? [])
    // 刷新配置
    toggle.value = !toggle.value
    nextTick(computedFixedHeight)
  }
)
// 按钮权限tab变更
watch(
  () => tabs.value,
  () => {
    model.value.permissionList = tabs.value
  },
  {
    deep: true
  }
)
// 更改权限码
watch(
  configPermissionCodes,
  () => {
    configResourceCode.value === 'menu'
      ? (model.value.permissionCodes = configPermissionCodes.value)
      : model.value.permissionList
      ? (model.value.permissionList[parseInt(activeTab.value, 10)].permissionCodes = configPermissionCodes.value)
      : ''
  },
  {
    deep: true
  }
)
</script>
<template>
  <el-empty v-if="empty" description="选择左侧菜单后操作" />
  <div v-else v-loading="submitLoading" class="form-container">
    <el-row :gutter="5" class="context-body">
      <el-col :md="24" :lg="10" class="left-container">
        <el-space wrap :fill="true" class="form-space-container">
          <div class="card-header">{{ model.displayName || '菜单配置' }}</div>
          <el-scrollbar :height="height">
            <el-form ref="formRef" :model="model" :rules="rules" label-width="100px">
              <el-form-item label="上级目录" prop="parentId">
                <el-input :model-value="model.parentId === '0' ? '顶级目录' : model.parentDisplayName" disabled />
              </el-form-item>
              <el-form-item label="分类" prop="displayType">
                <el-radio-group v-model="model.displayType" @change="handleChangeDisplayType">
                  <el-radio-button label="CATALOGUE">目录</el-radio-button>
                  <el-radio-button label="MENU">菜单</el-radio-button>
                  <el-radio-button label="OUTSIDE_URL">外链</el-radio-button>
                  <el-radio-button label="IFRAME">iframe</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="图标">
                <icon-select v-if="model.routeMeta && model.routeMeta.icon" v-model="model.routeMeta.icon" />
              </el-form-item>
              <el-form-item label="名称" prop="displayName">
                <el-input v-model="model.displayName" placeholder="请输入名称" clearable />
              </el-form-item>
              <el-form-item label="编码">
                <route-select
                  v-if="displayFields?.selectResourceCode && model?.routeMeta"
                  v-model="model.routeMeta.resourceCode"
                  v-model:component-path="model.routeMeta.componentPath"
                />
                <el-input v-else v-model="model.resourceCode" placeholder="请输入编码" clearable />
              </el-form-item>
              <el-form-item
                v-if="model?.routeMeta?.componentPath && displayFields?.selectResourceCode"
                label="组件地址"
              >
                <el-input v-model="model.routeMeta.componentPath" disabled />
              </el-form-item>
              <el-form-item label="路由地址">
                <el-input v-model="model.routePath" placeholder="请输入路由地址" clearable />
              </el-form-item>
              <el-form-item v-if="displayFields?.redirectPath" label="重定向">
                <el-input v-model="model.redirectPath" placeholder="请输入重定向" clearable />
              </el-form-item>
              <el-form-item label="菜单权限接口">
                <permission-code-select
                  v-if="model"
                  v-model="model.permissionCodes"
                  type="menu"
                  @config="clickConfigPermission(model, false)"
                  @auto-refresh="autoRefreshPermissionCode(model)"
                />
              </el-form-item>
              <el-form-item label="状态">
                <el-switch
                  v-model="model.status"
                  active-value="A"
                  inactive-value="I"
                  active-text="有效"
                  inactive-text="无效"
                />
              </el-form-item>
              <el-form-item label="排序号">
                <el-input-number v-model="model.sortId" placeholder="请输入排序号" style="width: 100%" clearable />
              </el-form-item>
              <el-form-item>
                <template #label>
                  <div style="display: flex; align-items: center; justify-content: end">
                    <span>其他配置</span>
                    <el-tooltip effect="dark" placement="top-start">
                      <template #content>
                        隐藏：隐藏时菜单栏不会显示，但地址可以访问；<br />
                        缓存：页面开启keepAlive，缓存当前页面；<br />
                        忽略认证：当前页面访问不需要权限认证。<br />
                      </template>
                      <el-icon><InfoFilled /></el-icon>
                    </el-tooltip>
                  </div>
                </template>
                <el-checkbox v-if="model?.routeMeta" v-model="model.routeMeta.hidden" label="隐藏" />
                <el-checkbox v-if="model?.routeMeta" v-model="model.routeMeta.keepAlive" label="缓存" />
                <el-checkbox v-if="model?.routeMeta" v-model="model.routeMeta.ignoreAuth" label="忽略认证" />
              </el-form-item>
            </el-form>
            <div v-if="displayFields?.permissionList" class="btn-config-container">
              <div class="btn-config__header">
                <span>按钮权限配置</span>
                <el-button :icon="Plus" circle type="success" @click="handleAddTab" />
              </div>
              <div class="btn-config__body">
                <el-tabs v-model="activeTab" type="card" closable @tab-remove="removeTab" @tab-change="handleChangeTab">
                  <el-tab-pane
                    v-for="(permission, index) in model.permissionList"
                    :key="`tab_${index}`"
                    :label="permission.displayName"
                    :name="`${index}`"
                  >
                    <el-descriptions :column="1">
                      <el-descriptions-item label="按钮权限编码">
                        <el-row type="flex" align="middle" :gutter="16">
                          <el-col :span="16">
                            <el-select
                              v-if="btnResourceCodeSelect"
                              v-model="permission.resourceCode"
                              filterable
                              allow-create
                              placeholder="请选取当前按钮权限编码"
                              @change="(value: string) => changeBtnResourceCode(permission, value)"
                            >
                              <el-option
                                v-for="(item, i) in more.resourcePermissionCodeOptions"
                                :key="`frontend-code_${i}`"
                                :label="`${item.label}[${item.value}]`"
                                :value="item.value"
                              />
                            </el-select>
                            <el-input
                              v-else
                              v-model="permission.resourceCode"
                              placeholder="请输入按钮权限编码"
                              @input="(value: string) => changeBtnResourceCode(permission, value)"
                            />
                          </el-col>
                          <el-col :span="8">
                            <el-button
                              type="primary"
                              :icon="Refresh"
                              size="small"
                              @click="toggleBtnResourceCodeSelect(permission)"
                            >
                              {{ btnResourceCodeSelect ? '自定义输入' : '从字典选取' }}
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
                        <permission-code-select
                          v-model="permission.permissionCodes"
                          type="permission"
                          @config="clickConfigPermission(permission)"
                          @auto-refresh="autoRefreshPermissionCode(permission)"
                        />
                      </el-descriptions-item>
                    </el-descriptions>
                  </el-tab-pane>
                </el-tabs>
              </div>
            </div>
          </el-scrollbar>
        </el-space>
      </el-col>
      <el-col :md="24" :lg="14" class="right-container">
        <el-skeleton v-if="loadingRestPermissions" :rows="10" animated />
        <permission-code-list
          v-if="model?.routeMeta"
          v-model:permission-codes="configPermissionCodes"
          :title="configPermissionTitle"
          :toggle="toggle"
          :config-code="configResourceCode"
          :menu-resource-code="model.routeMeta.resourceCode"
          :rest-permissions="restPermissions"
        />
      </el-col>
    </el-row>
    <div v-if="!empty" class="is-fixed btn-fixed">
      <el-button type="primary" @click="submitForm(formRef)">保存</el-button>
      <el-button @click="resetForm(formRef)">重置</el-button>
    </div>
  </div>
</template>
<style scoped lang="scss">
.form-container {
  position: relative;
  height: 100%;
  .context-body {
    width: 100%;
  }
  .is-fixed {
    box-sizing: border-box;
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    border-top: 1px solid var(--el-border-color);
    padding: 5px 16px;
    background: var(--el-bg-color);
    text-align: center;
    z-index: 1;
  }
}

.left-container {
  padding: 10px !important;
  border-right: 1px solid var(--el-color-info-light-9);
  .el-space {
    width: 100%;
  }
  .custom-alert-tip {
    padding: 0;
    width: auto;
  }
}
.right-container {
  padding: 10px !important;
}
.btn-config-container {
  width: 100%;
  .btn-config__header {
    display: flex;
    justify-content: space-between;
    padding: 10px 10px 10px 0;
    &-left {
    }
  }
}
</style>
