<template>
  <el-descriptions class="permission-group" :title="`${permissionGroup.name}（${permissionGroup.code}）`" border :column="1" size="small">
    <el-descriptions-item  :key="`group-item-${_uid}_${index}`" v-for="(apiPermission, index) in permissionGroup.apiPermissionList">
      <div @click.stop.prevent="() => changeCheck(apiPermission.code)" :id="apiPermission.code" class="permission-group-code permission-group-text-overflow" slot="label">
        <el-checkbox :value="permissionCodeList.includes(apiPermission.code)">
          <el-tooltip placement="top">
            <template slot="content">
              {{apiPermission.code}} （{{apiPermission.label}}）
            </template>
            <span>{{apiPermission.code}}</span>
          </el-tooltip>
        </el-checkbox>
      </div>
      <template v-if="apiPermission.apiUriList && apiPermission.apiUriList.length > 0">
        <div @click.stop.prevent="changeCheck(apiPermission.code)">
          <div class="permission-group-api permission-group-text-overflow" :key="`${apiPermission.code}_api-uri-${index}`" v-for="(apiUri, index) in apiPermission.apiUriList">
            <el-tooltip  placement="top">
              <template slot="content">
                {{apiUri.method}}:{{apiUri.uri}}（{{apiUri.label}}）
              </template>
              <span>{{apiUri.method}}:{{apiUri.uri}}（{{apiUri.label}}）</span>
            </el-tooltip>
          </div>
        </div>
      </template>
    </el-descriptions-item>
  </el-descriptions>
</template>

<script>
export default {
  name: 'PermissionGroup',
  methods: {
    changeCheck(code) {
      this.$emit('changePermissionCode',
        this.permissionCodeList.includes(code) ? 'remove' : 'add',
        code
      )
    }
  },
  props: {
    permissionGroup: {
      type: Object,
      required: true
    },
    permissionCodeList: {
      type: Array,
      default: () => []
    }
  }
}
</script>

<style scoped lang="scss" rel="stylesheet/scss">
.permission-group {
  /deep/.el-descriptions__body > table {
    table-layout: fixed;
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
    background-color: #e2f6fe;
    border-radius: 5px;
    transition: 2s;
  }
  &-api {
    border-bottom: 1px solid #e8e8e8;
    padding: 8px 16px;
    &:last-child {
      border-bottom: 0;
    }
  }
}

</style>
