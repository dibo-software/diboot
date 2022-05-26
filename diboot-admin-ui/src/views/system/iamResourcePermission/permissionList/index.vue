<script lang="ts" setup>
import PermissionGroup from './PermissionGroup.vue'
import { useVueFuse } from 'vue-fuse'
import { defineProps, reactive, Ref, withDefaults } from 'vue'
import type { PermissionGroupType, ApiUri, ApiPermission, SelectOption, FusePermission } from '../type'
type Props = {
  title: string
  configCode: string
  menuResourceCode?: string
  currentPermissionCodes: string[]
  originApiList?: Array<PermissionGroupType>
}
const props = withDefaults(defineProps<Props>(), {
  originApiList: () => []
})

let permissionCodeList = reactive<string[]>([])
let searchVal = ref('')

const computedFusePermissionDatas = computed(() => {
  const fusePermissionDatas: Array<FusePermission> = []
  props.originApiList?.forEach((item: PermissionGroupType) => {
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
  () => props.currentPermissionCodes,
  val => {
    permissionCodeList = val
  }
)
watch([() => props.configCode, () => props.menuResourceCode], () => {
  goScrollIntoView(getAnchor())
})

onMounted(() => {
  permissionCodeList = props.currentPermissionCodes
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
const emits = defineEmits(['changePermissionCodes'])
/**
 * 更改权限码（添加或删除）
 * @param type
 * @param code
 */
const changePermissionCode = (type: string, code: string) => {
  if (type === 'add') {
    permissionCodeList.push(code)
  } else {
    permissionCodeList = permissionCodeList.filter((item: string) => item !== code)
  }
  emits('changePermissionCodes', permissionCodeList)
}
const searchChange = (value: string) => {
  if (value && value.includes('#')) {
    const id = value.split('#')[0]
    goScrollIntoView(id)
    const idElement: HTMLElement | null = document.getElementById(id)
    idElement?.classList.add('light-high')
    const time = setTimeout(() => {
      idElement?.classList.remove('light-high')
      clearTimeout(time)
    }, 4000)
  }
  searchVal.value = value
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
const goScrollIntoView = async (value: string) => {
  await nextTick()
  if (value) {
    document.getElementById(value)?.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' })
  } else {
    document.getElementById('permissionListGroups')?.scrollIntoView({ behavior: 'smooth' })
  }
}
</script>
<template>
  <el-card :header="title" shadow="never" class="permission-list-container">
    <div class="permission-list-header">
      <el-select
        remote
        :value="searchVal"
        placeholder="搜索需要设置的接口：支持标题、权限码、接口地址模糊搜索"
        style="width: 100%"
        filterable
        clearable
        :remote-method="(val: string) => (search = val)"
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
    <div class="permission-list-groups">
      <div id="permissionListGroups">
        <permission-group
          v-for="(permissionGroup, index) in originApiList"
          :key="`permission_group_${index}`"
          :permission-code-list="permissionCodeList"
          :permission-group="permissionGroup"
          @change-permission-code="changePermissionCode"
        />
      </div>
    </div>
  </el-card>
</template>

<style scoped lang="scss" rel="stylesheet/scss">
.permission-list-container {
  .permission-list-header {
    margin-bottom: 10px;
  }
  .permission-list-groups {
    height: calc(70vh - 260px);
    overflow: hidden;
    overflow-y: auto;
    &__head {
      margin-bottom: 10px;
      font-weight: bold;
      background-color: #e3e3e3;
    }
  }
}
</style>
