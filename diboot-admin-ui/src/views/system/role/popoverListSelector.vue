<script setup lang="ts" name="PopverListSelector">
import RoleListSelector from '@/views/system/role/listSelector.vue'
import { ElMessage } from 'element-plus'
import type { Role } from '@/views/system/role/type'
type Props = {
  modelValue: string
  multi?: boolean
}
const props = withDefaults(defineProps<Props>(), {
  modelValue: '',
  multi: false
})
const emit = defineEmits(['update:modelValue'])

const selectOptions = ref<Record<string, string>[]>([])
const selectedRows = ref<Role[]>([])

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
const selectedKeys = computed<string[]>(() => {
  const { multi } = props
  if (multi) {
    return optionsValue.value as any
  } else {
    return [optionsValue.value]
  }
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

const onSelect = (rows: Role[]) => {
  const { multi } = props
  const labelValueList = rows.map(item => {
    return {
      label: item.name,
      value: item.id
    }
  })
  selectOptions.value = labelValueList
  const valList = labelValueList.map(item => item.value)
  if (!multi) {
    optionsValue.value = valList.length > 0 ? valList[0] : ''
  } else {
    optionsValue.value = valList
  }
}
const loadInitOptions = async (value: string | string[]) => {
  if (!value) {
    return false
  }
  let optionsValues = value instanceof Array ? value : [value]
  optionsValues = [...new Set(optionsValues)]
  const ids = optionsValues.join(',')
  await loadInitOptionsFromRemote(ids)
}

const loadInitOptionsFromRemote = async (ids: string) => {
  const res = await api.get<Role[]>('/role/listByIds', { ids })
  if (res.code === 0) {
    const { data } = res
    if (data && data.length > 0) {
      selectedRows.value = data
      selectOptions.value = data.map(
        (item: Role) =>
          ({
            label: item.name,
            value: item.id
          } as Record<string, string>)
      )
    }
  } else {
    ElMessage.warning('未加载到选项初始数据')
  }
}
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
