<script lang="ts" setup>
import { useVueFuse } from 'vue-fuse'
import type { ElScrollbar } from 'element-plus'
import type { RestPermission, ApiUri, ApiPermission, SelectOption, FusePermission } from '../type'
import { getElementAbsoluteLocation } from '@/utils/document'

// props
const props = defineProps<{
  appModule?: string
  menuType?: string
  menuCode?: string
  displayName?: string
  permissionCodes?: Array<string>
}>()

const emits = defineEmits<{
  (e: 'update:permissionCodes', permissionCodes: string[]): void
  (e: 'moduleList', moduleList: string[]): void
}>()

const loading = ref(true)
// 后端权限资源，list形式存储
const restPermissions: RestPermission[] = reactive([])
// 后端权限资源，基于appModule对后端权限分组
const moduleRestPermissionMap = ref<Record<string, RestPermission[]>>({})

watch(
  () => props.appModule,
  module => {
    restPermissions.length = 0
    restPermissions.push(...(moduleRestPermissionMap.value[module ?? ''] || []))
  }
)

const moduleList = computed(() => Object.keys(moduleRestPermissionMap.value))
watch(moduleList, value => emits('moduleList', value))

api
  .get<RestPermission[] | Record<string, RestPermission[]>>('/iam/resource/api-list')
  .then(res => {
    if (Array.isArray(res.data)) restPermissions.push(...res.data)
    else moduleRestPermissionMap.value = res.data ?? {}
  })
  .catch(err => {
    ElMessage.error(err.msg || err)
  })
  .finally(() => {
    loading.value = false
  })

// data
const permissionGroupsScrollbarRef = ref<InstanceType<typeof ElScrollbar>>()
const searchVal = ref('')

// 计算模糊搜索数据
const computedFusePermissionDatas = computed(() => {
  const fusePermissionDatas: Array<FusePermission> = []
  restPermissions?.forEach((item: RestPermission) => {
    const permissionGroup = `${item.name}（${item.code}）`
    const apiPermissionList = item.apiPermissionList
    if (apiPermissionList && apiPermissionList.length > 0) {
      apiPermissionList.forEach((apiPermission: ApiPermission) => {
        const permissionCode = apiPermission.code
        const permissionCodeLabel = apiPermission.label
        const apiUriList = apiPermission.apiUriList
        if (apiUriList && apiUriList.length > 0) {
          apiUriList.forEach((apiUri: ApiUri) => {
            const uri = `${apiUri.method}:${apiUri.uri}（${apiUri.label}）`
            fusePermissionDatas.push({
              title: uri,
              permissionGroup,
              permissionCode,
              permissionCodeLabel
            })
          })
        }
      })
    }
  })
  return fusePermissionDatas
})

defineExpose({
  async relocation() {
    await nextTick()
    let anchor = ''
    if (props.permissionCodes && props.permissionCodes.length > 0) {
      anchor = props.permissionCodes[0]
    } else if (props.menuCode) {
      handleRemoteMethod(props.menuCode)
      nextTick(() => {
        if (results.value && results.value.length > 0) {
          anchor = results.value[0].permissionCode
          // 清空搜索数据
          handleRemoteMethod('')
        }
      })
    }
    goScrollIntoView(anchor, false)
  }
})

// 模糊搜索
const { search, results } = useVueFuse<SelectOption>(computedFusePermissionDatas, {
  // 是否按优先级进行排序
  shouldSort: true,
  // 匹配度阈值	0.0表示完全匹配(字符和位置)；1.0将会匹配所有值
  threshold: 0.3,
  // 将被搜索的键列表。 这支持嵌套路径、加权搜索、在字符串和对象数组中搜索。
  // name：搜索的键 uri(api), permissionGroup(所属对象), permissionCode（权限code）, permissionCodeLabel（权限名称）
  // weight：对应的权重
  keys: [
    {
      name: 'permissionCode',
      weight: 0.9
    },
    {
      name: 'title',
      weight: 0.7
    },
    {
      name: 'permissionGroup',
      weight: 0.3
    },
    {
      name: 'permissionCodeLabel',
      weight: 0.2
    }
  ]
})

// 本地方法
// 远程调用方法
const handleRemoteMethod = (val: string | unknown) => {
  search.value = val as string
}
// 更改权限code
const handleChangePermissionCode = (code: string) => {
  let tempList = props.permissionCodes ?? []
  if (!props.permissionCodes?.includes(code)) tempList.push(code)
  else tempList = tempList.filter((item: string) => item !== code)
  emits('update:permissionCodes', tempList)
}
/**
 * 搜索数据
 * @param value
 */
const handleSearchChange = (value: string) => {
  const id = value && value.includes('#') ? value.split('#')[0] : undefined
  goScrollIntoView(id)
}
/**
 * 前往指定搜索的指定view
 * @param value
 */
const goScrollIntoView = async (value?: string, allowHighLight = true) => {
  await nextTick()
  if (!value) {
    permissionGroupsScrollbarRef.value?.setScrollTop(0)
  }
  // 权限列表容器高度
  const permissionGroupsElement = document.getElementById('permissionGroups')
  const permissionGroupsElementLocation = getElementAbsoluteLocation(permissionGroupsElement)
  if (!permissionGroupsElementLocation) return
  const element: HTMLElement | null = document.getElementById(value as string)
  const searchPermissionLocation = getElementAbsoluteLocation(element)
  if (!searchPermissionLocation) return
  permissionGroupsScrollbarRef.value?.setScrollTop(
    searchPermissionLocation.absoluteTop - permissionGroupsElementLocation.absoluteTop - 20
  )
  if (allowHighLight) {
    // 高亮数据
    element?.classList.add('light-high')
    const time = setTimeout(() => {
      clearTimeout(time)
      element?.classList.remove('light-high')
    }, 4000)
  }
}
</script>
<template>
  <el-skeleton v-if="loading" :rows="10" animated />
  <div v-else>
    <div v-if="menuType !== 'MENU'" style="padding: 8px">
      <el-alert :title="$t('resource.permissionSelect.title')" type="warning" :closable="false" />
    </div>
    <el-space v-else wrap :fill="true" class="permission-list-container">
      <div style="margin: 8px; zoom: 1.1">
        {{ displayName }}
        <span style="color: var(--el-text-color-secondary); margin-left: 5px">{{}}</span>
      </div>
      <el-select
        v-model="searchVal"
        remote
        :placeholder="$t('resource.permissionSelect.searchPlaceholder')"
        filterable
        clearable
        :remote-method="handleRemoteMethod"
        @change="handleSearchChange"
      >
        <el-option
          v-for="(options, index) in results"
          :key="`search_${index}`"
          :label="options.title"
          :value="`${options.permissionCode}#${options.title}`"
        />
      </el-select>
      <el-alert
        v-if="moduleList.length > 0 && !appModule"
        :title="$t('resource.permissionSelect.tip')"
        type="success"
        :closable="false"
      />
      <el-scrollbar ref="permissionGroupsScrollbarRef" height="calc(100vh - 223px)">
        <div id="permissionGroups" class="permission-groups">
          <el-descriptions
            v-for="(restPermission, index) in restPermissions"
            :key="`rest-permission_${index}`"
            class="permission-group"
            :title="`${restPermission.name}（${restPermission.code}）`"
            border
            :column="1"
            size="small"
          >
            <el-descriptions-item
              v-for="(apiPermission, indx) in restPermission.apiPermissionList"
              :key="`api-permission_${indx}`"
            >
              <template #label>
                <div
                  :id="apiPermission.code"
                  class="permission-group-code permission-group-text-overflow"
                  @click.stop.prevent="() => handleChangePermissionCode(apiPermission.code)"
                >
                  <el-checkbox :key="`${permissionCodes}`" :checked="permissionCodes?.includes(apiPermission.code)">
                    <el-tooltip placement="top" :show-after="200">
                      <template #content> {{ apiPermission.code }} （{{ apiPermission.label }}） </template>
                      <span>{{ apiPermission.code }}</span>
                    </el-tooltip>
                  </el-checkbox>
                </div>
              </template>
              <template v-if="apiPermission.apiUriList && apiPermission.apiUriList.length > 0">
                <div @click.stop.prevent="handleChangePermissionCode(apiPermission.code)">
                  <div
                    v-for="(apiUri, idx) in apiPermission.apiUriList"
                    :key="`${apiPermission.code}_api-uri-${idx}`"
                    class="permission-group-api permission-group-text-overflow"
                  >
                    <el-tooltip placement="top" :show-after="200">
                      <template #content> {{ apiUri.method }}:{{ apiUri.uri }}（{{ apiUri.label }}） </template>
                      <span>{{ apiUri.method }}:{{ apiUri.uri }}（{{ apiUri.label }}）</span>
                    </el-tooltip>
                  </div>
                </div>
              </template>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </el-scrollbar>
    </el-space>
  </div>
</template>

<style scoped lang="scss" rel="stylesheet/scss">
.permission-list-container {
  width: 100%;
  .permission-groups {
    &__head {
      margin-bottom: 10px;
      font-weight: bold;
    }
    .permission-group {
      :deep(.el-descriptions__body) {
        table {
          table-layout: fixed;
        }
        .el-descriptions-item__content {
          padding: 0;
        }
      }
      &-text-overflow {
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;
        cursor: pointer;
        .ant-checkbox-wrapper {
          width: 100%;
        }
      }
      .light-high {
        background-color: var(--el-color-primary-light-5);
        border-radius: 5px;
        padding: 0 10px;
        transition: 0.3s;
      }
      &-api {
        border-bottom: 1px solid var(--el-border-color);
        padding: 8px 16px;
        &:last-child {
          border-bottom: 0;
        }
      }
      .el-descriptions--small .el-descriptions__header {
        margin-top: 5px;
        margin-bottom: 5px;
      }
    }
  }
}
</style>
