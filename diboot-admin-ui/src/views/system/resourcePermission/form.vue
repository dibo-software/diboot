<script lang="ts" setup>
import PermissionList from './permissionList/index.vue'
import RouteSelect from './modules/RouteSelect.vue'
import PermissionCodeConfig from './modules/PermissionCodeConfig.vue'
import { Plus, Refresh, InfoFilled } from '@element-plus/icons-vue'

import type { FormInstance, FormRules } from 'element-plus'
import type { ResourcePermission } from './type'
import useDisplayControl from './hooks/displayControl'
import usePermissionControl from './hooks/permissionControl'
import useScrollbarHeight from './hooks/scrollbarHeight'
import type { MenuType } from './hooks/displayControl'
let permissionCodes = reactive<string[]>([])
const showPermission = ref(false)
const submitForm = async (formEl: FormInstance | undefined) => {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log(model)
      console.log('submit!')
    } else {
      console.log('error submit!', fields)
    }
  })
}
const resetForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.resetFields()
}
const handleChangePermissionCodes = (paramPermissionCodes: string[]) => {
  permissionCodes = paramPermissionCodes
  // if (currentConfigCode.value === 'Menu') {
  //   permissionCodes = paramPermissionCodes
  // } else {
  //   // permissionList[currentPermissionActiveKey.value].permissionCodes = paramPermissionCodes
  // }
}
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
const visibleHeight = inject<number>('visibleHeight')
// ======> 本地方法
// 切换菜单类型
const handleChangeDisplayType = (val: string | number | boolean) => {
  changeDisplayType(val as MenuType)
}
// 添加按钮权限
const handleAddTab = () => {
  addTab(_.cloneDeep(NEW_PERMISSION_ITEM))
}
// 权限配置

// compute
const existPermissionCodes = computed(() => {
  if (!tabs.value) return []
  return tabs.value.map(item => item.resourceCode as string)
})
// ======> hook相关
// 滚动高度计算hook
const { height, computedFixedHeight } = useScrollbarHeight({
  fixedBoxSelectors: ['.form-space-container>.el-space__item:first-child', '.btn-fixed'],
  visibleHeight,
  extraHeight: 15
})
// 菜单类型切换控制字段
const { displayFields, changeDisplayType } = useDisplayControl()

// 权限相关hooks
const {
  btnResourceCodeSelect,
  configPermissionTitle,
  configResourceCode,
  configPermissionCodes,
  restPermissions,
  initRestPermissions,
  initResourcePermissionCodeOptions,
  changeBtnResourceCode,
  changeBtnPermissionName,
  toggleBtnResourceCodeSelect,
  clickConfigPermission
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
    }
  }
})

// 监听
// 菜单树切换变更
watch(
  () => props.formValue,
  val => {
    empty.value = false
    model.value = val
    changeDisplayType(val.displayType as MenuType)
    initTabs(val.permissionList ?? [])
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
</script>
<template>
  <el-empty v-if="empty" description="选择左侧菜单后操作" />
  <div v-else class="form-container">
    <el-row :gutter="5" class="context-body">
      <el-col :md="24" :lg="10" class="left-container">
        <el-space wrap :fill="true" class="form-space-container">
          <div class="card-header">{{ model.displayName || '菜单配置' }}</div>
          <el-scrollbar :height="height">
            <el-form ref="formRef" :model="model" :rules="rules" label-width="90px">
              <el-form-item label="上级目录" prop="parentId">
                <el-input :modelValue="model.parentId === '0' ? '顶级目录' : model.parentDisplayName" disabled />
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
                <icon-select v-model="model.routeMeta.icon" />
              </el-form-item>
              <el-form-item label="名称" prop="displayName">
                <el-input v-model="model.displayName" placeholder="请输入名称" clearable />
              </el-form-item>
              <el-form-item label="编码">
                <route-select
                  v-if="displayFields.selectResourceCode"
                  v-model="model.routeMeta.resourceCode"
                  v-model:component-path="model.routeMeta.componentPath"
                />
                <el-input v-else v-model="model.resourceCode" placeholder="请输入编码" clearable />
              </el-form-item>
              <el-form-item v-if="model.routeMeta.componentPath && displayFields.selectResourceCode" label="组件地址">
                <el-input v-model="model.routeMeta.componentPath" disabled />
              </el-form-item>
              <el-form-item label="路由地址">
                <el-input v-model="model.routePath" placeholder="请输入路由地址" clearable />
              </el-form-item>
              <el-form-item v-if="displayFields.redirectPath" label="重定向">
                <el-input v-model="model.redirectPath" placeholder="请输入重定向" clearable />
              </el-form-item>
              <el-form-item label="菜单权限接口">
                <permission-code-config
                  v-model="model.permissionCodes"
                  type="menu"
                  @config="clickConfigPermission('menu', model)"
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
                <el-checkbox v-model="model.routeMeta.hidden" label="隐藏" />
                <el-checkbox v-model="model.routeMeta.keepAlive" label="缓存" />
                <el-checkbox v-model="model.routeMeta.ignoreAuth" label="忽略认证" />
              </el-form-item>
            </el-form>
            <div v-if="displayFields.permissionList" class="btn-config-container">
              <div class="btn-config__header">
                <span>按钮权限配置</span>
                <el-button :icon="Plus" circle type="success" @click="handleAddTab" />
              </div>
              <div class="btn-config__body">
                <el-tabs v-model="activeTab" type="card" closable @tab-remove="removeTab">
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
                              @change="value => changeBtnResourceCode(permission, value)"
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
                              @input="value => changeBtnResourceCode(permission, value)"
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
                          @input="value => changeBtnPermissionName(permission, value)"
                        />
                      </el-descriptions-item>
                      <el-descriptions-item label="按钮权限接口">
                        <permission-code-config
                          v-model="permission.permissionCodes"
                          type="permission"
                          @config="clickConfigPermission('permission', permission)"
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
        <permission-list
          ref="permissionListRef"
          :title="configPermissionTitle"
          :permission-codes="configPermissionCodes"
          :config-code="configResourceCode"
          :rest-permissions="restPermissions"
          @change="handleChangePermissionCodes"
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
  padding: 0 10px;
  .context-body {
    width: 100%;
  }
  .is-fixed {
    box-sizing: border-box;
    position: absolute;
    left: 0;
    bottom: 0;
    width: 100%;
    border-top: 1px solid #e9e9e9;
    padding: 10px 16px 0 16px;
    background: #fff;
    text-align: center;
    z-index: 1;
  }
}

.left-container {
  border-right: 1px solid var(--el-color-info-light-9);
  padding-right: 15px !important;
  .el-space {
    width: 100%;
  }
  .custom-alert-tip {
    padding: 0;
    width: auto;
  }
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
