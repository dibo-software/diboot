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
        :remote-method="handleSearch"
        @change="handleSearchChange"
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
            :key="`permission_group_${_uid}_${index}`"
            :permission-code-list="permissionCodeList"
            :permission-group="permissionGroup"
            @changePermissionCode="handleChangePermissionCode"
          />
        </template>
      </div>
    </div>
  </el-card>
</template>

<script>
import PermissionGroup from '@/views/system/iamResourcePermission/permissionList/PermissionGroup'
import Fuse from 'fuse.js'

export default {
  name: 'PermissionListIndex',
  components: { PermissionGroup },
  data() {
    return {
      permissionCodeList: this.currentPermissionCodes,
      searchVal: undefined,
      fuse: null,
      searchOptions: []
    }
  },
  computed: {
    fusePermissionDatas() {
      const fusePermissionDatas = []
      this.originApiList.forEach(item => {
        const permissionGroup = `${item.name}（${item.code}）`
        const apiPermissionList = item.apiPermissionList
        if (apiPermissionList && apiPermissionList.length > 0) {
          apiPermissionList.forEach(apiPermission => {
            const permissionCode = apiPermission.code
            const permissionCodeLabel = apiPermission.label
            const apiUriList = apiPermission.apiUriList
            if (apiUriList && apiUriList.length > 0) {
              apiUriList.forEach(apiUri => {
                const uri = `${apiUri.method}:${apiUri.uri}（${apiUri.label}）`
                fusePermissionDatas.push({
                  title: uri, permissionGroup, permissionCode, permissionCodeLabel
                })
              })
            }
          })
        }
      })
      return fusePermissionDatas
    }
  },
  watch: {
    currentPermissionCodes(val) {
      this.permissionCodeList = val
    },
    configCode() {
      this.goScrollIntoView(this.getAnchor())
    },
    menuResourceCode() {
      this.goScrollIntoView(this.getAnchor())
    }
  },
  mounted() {
    this.initFuse()
  },
  methods: {
    initFuse() {
      this.fuse = new Fuse(this.fusePermissionDatas, {
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
    },
    /**
     * 更改权限码（添加或删除）
     */
    handleChangePermissionCode(type, code) {
      if (type === 'add') {
        this.permissionCodeList.push(code)
      } else {
        this.permissionCodeList = this.permissionCodeList.filter(item => item !== code)
      }
      this.$emit('changePermissionCodes', this.permissionCodeList)
    },
    handleSearch(value) {
      this.searchOptions = value !== '' ? this.fuse.search(value) : []
    },
    /**
     * 根据值跳转至指定内容区域
     * @param value
     */
    handleSearchChange(value) {
      if (value && value.includes('#')) {
        const id = value.split('#')[0]
        this.goScrollIntoView(id)
        const idElement = document.getElementById(id)
        idElement.classList.add('light-high')
        const time = setTimeout(() => {
          idElement.classList.remove('light-high')
          clearTimeout(time)
        }, 4000)
      } else {
        this.searchOptions = []
      }
      this.searchVal = value
    },
    /**
     * 获取锚点
     */
    getAnchor() {
      let anchor = ''
      if (this.permissionCodeList && this.permissionCodeList.length > 0) {
        anchor = this.permissionCodeList[0]
      } else {
        if (this.menuResourceCode) {
          const searchResult = this.fuse.search(this.menuResourceCode)
          if (searchResult && searchResult.length > 0) {
            anchor = searchResult[0].permissionCode
          }
        }
      }
      return anchor
    },
    /**
     * 前往指定的位置
     * @param value
     */
    goScrollIntoView(value) {
      this.$nextTick(() => {
        if (value) {
          document.getElementById(value).scrollIntoView({ behavior: 'smooth', block: 'center', inline: 'nearest' })
        } else {
          document.getElementById('permissionListGroups').scrollIntoView({ behavior: 'smooth' })
        }
      })
    }
  },
  props: {
    title: {
      type: String,
      required: true
    },
    configCode: {
      type: String,
      required: true
    },
    menuResourceCode: {
      type: String,
      required: true
    },
    currentPermissionCodes: {
      type: Array,
      default: () => []
    },
    originApiList: {
      type: Array,
      default: () => []
    }
  }
}
</script>

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
