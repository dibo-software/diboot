<script lang="ts" setup>
import useScrollbarHeight from '../hooks/scrollbarHeight'
import { useVueFuse } from 'vue-fuse'
import { ElScrollbar } from 'element-plus'
import type { RestPermission, ApiUri, ApiPermission, SelectOption, FusePermission } from '../type'
import { getElementAbsoluteLocation } from '@/utils/document'
type Props = {
  title: string
  toggle: boolean
  configCode: string
  menuResourceCode?: string | unknown
  permissionCodes: string[]
  restPermissions?: Array<RestPermission>
}
// props
const props = withDefaults(defineProps<Props>(), {
  restPermissions: () => []
})
const boxHeight = inject<number>('boxHeight', 0)
// hooks
// 滚动高度计算hook
const { height, computedFixedHeight } = useScrollbarHeight({
  boxHeight,
  fixedBoxSelectors: ['.permission-list-container>.permission-list-header', '.btn-fixed'],
  extraHeight: 30
})
// data
const permissionGroupsScrollbarRef = ref<InstanceType<typeof ElScrollbar>>()
const searchVal = ref('')
const reload = ref(true)
const reloadKey = ref('reload_true')

// 计算模糊搜索数据
const computedFusePermissionDatas = computed(() => {
  const fusePermissionDatas: Array<FusePermission> = []
  props.restPermissions?.forEach((item: RestPermission) => {
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

/**
 * configCode、menuResourceCode、toggle变更触发滚动
 */
watch([() => props.configCode, () => props.menuResourceCode, () => props.toggle], async () => {
  goScrollIntoView(await getAnchor(), false)
  _reload()
})

onMounted(() => {
  nextTick(computedFixedHeight)
})

// 模糊搜索
let { search, results } = useVueFuse<SelectOption>(computedFusePermissionDatas, {
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
const emits = defineEmits<{
  (e: 'update:permissionCodes', permissionCodes: string[]): void
}>()
/**
 * 获取跳转锚点
 */
const getAnchor = async () => {
  let anchor = ''
  if (props.permissionCodes && props.permissionCodes.length > 0) {
    anchor = props.permissionCodes[0]
  } else {
    if (props.menuResourceCode) {
      handleRemoteMethod(props.menuResourceCode)
      await nextTick()
      if (results.value && results.value.length > 0) {
        anchor = results.value[0].permissionCode
        // 清空搜索数据
        handleRemoteMethod('')
      }
    }
  }
  return anchor
}

// 本地方法
// 远程调用方法
const handleRemoteMethod = (val: string | unknown) => {
  search.value = val as string
}
// 更改权限code
const handleChangePermissionCode = (code: string) => {
  let tempList = _.clone(props.permissionCodes)
  if (!props.permissionCodes.includes(code)) tempList.push(code)
  else tempList = tempList.filter((item: string) => item !== code)
  _reload()
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
// 刷新
const _reload = () => {
  reload.value = !reload.value
  reloadKey.value = `reload_${reload.value}`
}
</script>
<template>
  <div class="permission-list-container">
    <div class="permission-list-header">
      <div>
        {{ title }}
      </div>
      <el-select
        v-model="searchVal"
        remote
        placeholder="搜索需要设置的接口：支持标题、权限码、接口地址模糊搜索"
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
    </div>
    <el-scrollbar ref="permissionGroupsScrollbarRef" :height="height">
      <div id="permissionGroups" :key="reloadKey" class="permission-groups">
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
                <el-checkbox :checked="permissionCodes.includes(apiPermission.code)">
                  <el-tooltip placement="top">
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
                  <el-tooltip placement="top">
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
  </div>
</template>

<style scoped lang="scss" rel="stylesheet/scss">
.permission-list-container {
  .permission-list-header {
    margin-bottom: 10px;
  }
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
        background-color: var(--el-color-primary);
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
    }
  }
}
</style>
