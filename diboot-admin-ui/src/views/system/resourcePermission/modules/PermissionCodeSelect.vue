<script setup lang="ts">
const props = defineProps<{ modelValue?: string[]; type: 'permission' | 'menu' }>()

// 事件定义
const emits = defineEmits<{
  (e: 'update:modelValue', value?: string[]): void
  (e: 'config'): void
}>()
/**
 * 删除配置的权限
 * @param val
 */
const close = (val?: string) => {
  emits(
    'update:modelValue',
    props.modelValue?.filter((item: string) => item !== val)
  )
}
/**
 * 配置权限
 */
const openPermissionConfig = () => {
  emits('config')
}
</script>
<template>
  <el-space class="permission-code-space">
    <div class="permission-tag-container">
      <template v-if="modelValue && modelValue.length > 0">
        <el-tag
          v-for="(permissionCode, idx) in modelValue"
          :key="`${type}_${idx}`"
          type="success"
          closable
          @close="close(permissionCode)"
        >
          {{ permissionCode }}
        </el-tag>
      </template>
      <template v-else>
        <span style="color: #d3d3d3">请点击右侧"配置按钮"选择权限接口</span>
      </template>
    </div>
    <el-button type="primary" @click="openPermissionConfig">配置</el-button>
  </el-space>
</template>
<style scoped lang="scss">
.permission-code-space {
  :deep(.el-space__item:first-child) {
    flex: 1;
  }
}
.permission-tag-container {
  flex: 1;
  border: 1px solid var(--el-border-color);
  padding: 5px;
  margin: 0 2px;
  min-height: 32px;
  .el-tag:first-child {
    margin-right: 5px;
  }
  .el-tag + .el-tag {
    margin-right: 5px;
    margin-bottom: 5px;
  }
}
</style>
