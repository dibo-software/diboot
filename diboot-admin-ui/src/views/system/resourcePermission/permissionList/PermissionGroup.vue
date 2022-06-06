<script lang="ts" setup>
import type { RestPermission } from '../type'

const props = withDefaults(defineProps<{ restPermission: RestPermission; permissionCodeList?: string[] }>(), {
  permissionCodeList: () => []
})
const emits = defineEmits<{
  (e: 'change', type: string, code: string): void
}>()

const checkCode = (code: string) => emits('change', props.permissionCodeList.includes(code) ? 'remove' : 'add', code)
</script>
<template>
  <el-descriptions
    class="permission-group"
    :title="`${restPermission.name}（${restPermission.code}）`"
    border
    :column="1"
    size="small"
  >
    <el-descriptions-item
      v-for="(apiPermission, index) in restPermission.apiPermissionList"
      :key="`group-item_${index}`"
    >
      <template #label>
        <div
          :id="apiPermission.code"
          class="permission-group-code permission-group-text-overflow"
          @click.stop.prevent="() => checkCode(apiPermission.code)"
        >
          <el-checkbox :value="permissionCodeList.includes(apiPermission.code)">
            <el-tooltip placement="top">
              <template #content> {{ apiPermission.code }} （{{ apiPermission.label }}） </template>
              <span>{{ apiPermission.code }}</span>
            </el-tooltip>
          </el-checkbox>
        </div>
      </template>
      <template v-if="apiPermission.apiUriList && apiPermission.apiUriList.length > 0">
        <div @click.stop.prevent="() => checkCode(apiPermission.code)">
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
</template>
<style scoped lang="scss" rel="stylesheet/scss">
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
    background-color: #e2f6fe;
    border-radius: 5px;
    transition: 2s;
  }
  &-api {
    border-bottom: 1px solid var(--el-border-color);
    padding: 8px 16px;
    &:last-child {
      border-bottom: 0;
    }
  }
}
</style>
