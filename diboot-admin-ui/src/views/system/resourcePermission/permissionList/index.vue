<script lang="ts" setup>
import PermissionGroup from './PermissionGroup.vue'
import useScrollbarHeight from '../hooks/scrollbarHeight'
import { useVueFuse } from 'vue-fuse'
import { Ref } from 'vue'
import { ElScrollbar } from 'element-plus'
import type { RestPermission, ResourcePermission, ApiUri, ApiPermission, SelectOption, FusePermission } from '../type'
import { getElementAbsoluteLocation } from '@/utils/document'
type Props = {
  title: string
  configCode: string
  menuResourceCode?: string
  permissionCodes: string[]
  restPermissions?: Array<RestPermission>
}
const props = withDefaults(defineProps<Props>(), {
  restPermissions: () => []
})
const visibleHeight = inject<number>('visibleHeight')
// hooks
// 滚动高度计算hook
const { height, computedFixedHeight } = useScrollbarHeight({
  fixedBoxSelectors: ['.permission-list-container>.permission-list-header', '.btn-fixed'],
  visibleHeight,
  extraHeight: 30
})
const permissionGroupsScrollbarRef = ref<InstanceType<typeof ElScrollbar>>()
let permissionCodeList = reactive<string[]>([])
const searchVal = ref('')
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
watch(
  () => props.permissionCodes,
  val => {
    permissionCodeList = val
  }
)
watch([() => props.configCode, () => props.menuResourceCode], () => {
  goScrollIntoView(getAnchor())
})

onMounted(() => {
  permissionCodeList = props.permissionCodes

  nextTick(computedFixedHeight)
})

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
 * 更改权限码（添加或删除）
 * @param type
 * @param code
 */
const changePermissionCode = (type: string, code: string) => {
  if (type === 'add') {
    permissionCodeList.push(code)
  } else {
    permissionCodeList.length = 0
    permissionCodeList.push(...permissionCodeList.filter((item: string) => item !== code))
  }
  emits('update:permissionCodes', permissionCodeList)
}
/**
 * 搜索
 * @param value
 */
const searchChange = (value: string) => {
  if (value && value.includes('#')) {
    const id = value.split('#')[0]
    goScrollIntoView(id)
    const idElement: HTMLElement | null = document.getElementById(id)
    idElement?.classList.add('light-high')
    const time = setTimeout(() => {
      clearTimeout(time)
      idElement?.classList.remove('light-high')
    }, 4000)
  } else {
    goScrollIntoView()
  }
}

const getAnchor = () => {
  let anchor = ''
  if (permissionCodeList && permissionCodeList.length > 0) {
    anchor = permissionCodeList[0]
  } else {
    if (props.menuResourceCode) {
      search = toRef(props, 'menuResourceCode') as Ref<string>
      if (results.value && results.value.length > 0) {
        anchor = results.value[0].permissionCode
      }
    }
  }
  return anchor
}
/**
 * 前往指定搜索的指定view
 * @param value
 */
const goScrollIntoView = async (value?: string) => {
  await nextTick()
  if (!value) {
    permissionGroupsScrollbarRef.value?.setScrollTop(0)
  }
  const permissionListGroupsElement = document.getElementById('permissionListGroups')
  const permissionListGroupsElementLocation = getElementAbsoluteLocation(permissionListGroupsElement)
  if (!permissionListGroupsElementLocation) return
  const element: HTMLElement | null = document.getElementById(value as string)
  const searchPermissionLocation = getElementAbsoluteLocation(element)
  if (!searchPermissionLocation) return
  permissionGroupsScrollbarRef.value?.setScrollTop(
    searchPermissionLocation.absoluteTop - permissionListGroupsElementLocation.absoluteTop - 20
  )
}

const handleRemoteMethod = (val: string) => {
  search.value = val
}
</script>
<template>
  <div class="permission-list-container">
    <div class="permission-list-header">
      <div>
        {{ title }}
      </div>
      <el-select
        remote
        v-model="searchVal"
        placeholder="搜索需要设置的接口：支持标题、权限码、接口地址模糊搜索"
        filterable
        clearable
        :remote-method="handleRemoteMethod"
        @change="searchChange"
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
      <div ref="permissionListGroupsRef" class="permission-list-groups">
        <div id="permissionListGroups">
          <permission-group
            v-for="(restPermission, index) in restPermissions"
            :key="`rest-permission_${index}`"
            :permission-code-list="permissionCodeList"
            :rest-permission="restPermission"
            @change="changePermissionCode"
          />
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>

<style scoped lang="scss" rel="stylesheet/scss">
.permission-list-container {
  .permission-list-header {
    margin-bottom: 10px;
  }
  .permission-list-groups {
    &__head {
      margin-bottom: 10px;
      font-weight: bold;
      background-color: #e3e3e3;
    }
  }
}
</style>
