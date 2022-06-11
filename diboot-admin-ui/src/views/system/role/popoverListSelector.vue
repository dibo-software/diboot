<script setup lang="ts" name="RolePopverListSelector">
import RoleListSelector from '@/views/system/role/listSelector.vue'
import type { Role } from '@/views/system/role/type'
import usePopoverListSelector from '@/hooks/popoverListSelector'
type Props = {
  modelValue: string
  multi?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  multi: false
})
const emit = defineEmits(['update:modelValue'])

const optionsValue = computed({
  get: function (): string[] | string {
    const { multi, modelValue } = props
    if (multi) {
      return modelValue ? modelValue.split(',') : []
    } else {
      return modelValue || ''
    }
  },
  set: function (v: string[] | string) {
    const { multi } = props
    if (multi) {
      emit('update:modelValue', (v as string[]).join(','))
    } else {
      emit('update:modelValue', v)
    }
  }
})

const { selectOptions, selectedRows, selectedKeys, onSelect, loadInitOptions } = usePopoverListSelector<Role>({
  baseApi: '/role',
  multi: props.multi,
  labelKey: 'name',
  valueKey: 'id',
  optionsValue
})

// 通过监听值的变化，来自动加载用于回显的选项列表数据
watch(
  () => props.modelValue,
  (val: string) => {
    if (val && selectOptions.value.length === 0) {
      // 根据value值来加载需要的回显项
      loadInitOptions(val)
    }
  },
  {
    immediate: true
  }
)
</script>
<template>
  <el-popover :teleported="false" trigger="click" placement="right" :width="580" title="角色选择">
    <template #reference>
      <el-select v-model="optionsValue" :teleported="false" class="popover-select" :multiple="props.multi">
        <el-option v-for="item in selectOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </template>
    <div class="content-container">
      <role-list-selector
        v-model:selected-rows="selectedRows"
        :multi="props.multi"
        :selected-keys="selectedKeys"
        @select="onSelect"
      />
    </div>
  </el-popover>
</template>
<style lang="scss" scoped>
.content-container {
  height: calc(60vh);
}
.popover-select {
  :deep(.el-select-dropdown) {
    display: none;
  }
  :deep(.el-select-dropdown__empty) {
    display: none;
  }
}
</style>
