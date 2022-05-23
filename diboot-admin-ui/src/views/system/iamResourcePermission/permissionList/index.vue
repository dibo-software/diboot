<script lang="ts" setup>
import PermissionGroup from './PermissionGroup.vue'
import { useVueFuse } from 'vue-fuse'
import { defineProps, reactive, withDefaults } from 'vue'
import type { PermissionGroupType, ApiUri, ApiPermission, SelectOption, FusePermission } from './type'
type Props = {
  title: string
  configCode: string
  menuResourceCode: string
  currentPermissionCodes?: string[]
  originApiList?: Array<PermissionGroupType>
}
const props = withDefaults(defineProps<Props>(), {
  currentPermissionCodes: [],
  originApiList: []
})

let permissionCodeList = reactive([])
let searchVal = ref('')
let searchOptions = reactive<Array<SelectOption>>([])

type Api = {
  name: string
  code: string
}
const computedFusePermissionDatas: FusePermission = computed((): FusePermission => {
  const fusePermissionDatas: FusePermission = []
  props.originApiList.value?.forEach((item: PermissionGroupType) => {
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
            } as FusePermission)
          })
        }
      })
    }
  })
  return fusePermissionDatas
})
watch(props.currentPermissionCodes, val => {
  permissionCodeList.value = val
})
watch([props.configCode, props.menuResourceCode], () => {
  goScrollIntoView(getAnchor())
})

onMounted(() => {
  permissionCodeList.value = props.currentPermissionCodes
  initFuse()
})

const initFuse = useVueFuse(fusePermissionDatas.value, {
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
    permissionCodeList.value.push(code)
  } else {
    permissionCodeList.value = permissionCodeList.value.filter(item => item !== code)
  }
  emits('changePermissionCodes', permissionCodeList)
}
const searchChange = (value: string) => {
  if (value && value.includes('#')) {
    const id = value.split('#')[0]
    goScrollIntoView(id)
    const idElement: HTMLElement = document.getElementById(id)
    idElement?.classList.add('light-high')
    const time = setTimeout(() => {
      idElement?.classList.remove('light-high')
      clearTimeout(time)
    }, 4000)
  } else {
    searchOptions.value = []
  }
  searchVal.value = value
}
const search = () => {
  searchOptions.value = value !== '' ? fuse.value?.search(value) : []
}
const getAnchor = () => {
  let anchor = ''
  if (permissionCodeList.value && permissionCodeList.value.length > 0) {
    anchor = permissionCodeList.values[0]
  } else {
    if (props.menuResourceCode.value) {
      const searchResult: FusePermission[] = fuse.value?.search(props.menuResourceCode)
      if (searchResult && searchResult.length > 0) {
        anchor = searchResult[0].permissionCode
      }
    }
  }
  return anchor
}
const goScrollIntoView = (value: string) => {
  nextTick(() => {
    if (value) {
      document.getElementById(value)?.scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' })
    } else {
      document.getElementById('permissionListGroups')?.scrollIntoView({ behavior: 'smooth' })
    }
  })
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
        :remote-method="search"
        @change="searchChange"
      >
        <el-option
          v-for="(options, index) in searchOptions"
          :key="`search_${index}`"
          :label="options.title"
          :value="`${options.permissionCode}#${options.title}`"
        />
      </el-select>
    </div>
    <div class="permission-list-groups">
      <div id="permissionListGroups">
        <template v-for="(permissionGroup, index) in originApiList">
          <permission-group
            :key="`permission_group_${index}`"
            :permission-code-list="permissionCodeList"
            :permission-group="permissionGroup"
            @changePermissionCode="changePermissionCode"
          />
        </template>
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
