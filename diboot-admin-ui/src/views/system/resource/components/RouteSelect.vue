<script setup lang="ts">
import { Loading, Warning } from '@element-plus/icons-vue'
import { buildViewMap } from '@/utils/route'

const viewMapLoading = ref(true)

const viewMap = ref<Record<string, string>>({})
buildViewMap().then(res => {
  viewMap.value = res
  viewMapLoading.value = false
})

const componentName = ref<string>()
const props = defineProps<{ modelValue?: string; componentPath?: string }>()
watch(
  () => props.modelValue,
  value => (componentName.value = value),
  { immediate: true }
)

// 事件定义
const emits = defineEmits<{
  (e: 'update:modelValue', value?: string): void
  (e: 'update:componentPath', value?: string): void
  (e: 'change', value?: string): void
}>()
// 更改组件 和 资源路径
const changeComponentName = (val?: string) => {
  emits('update:modelValue', val)
  emits('update:componentPath', val ? viewMap.value[val] : '')
  emits('change', val)
}
</script>

<template>
  <div style="width: 100%">
    <el-select
      v-model="componentName"
      :placeholder="$t('resource.placeholder.componentName')"
      clearable
      filterable
      :loading="viewMapLoading"
      @change="changeComponentName"
    >
      <el-option v-for="item in Object.keys(viewMap)" :key="item" :label="item" :value="item" />
    </el-select>
    <el-input :model-value="componentPath" disabled size="small">
      <template #suffix>
        <el-icon v-if="viewMapLoading">
          <Loading />
        </el-icon>
        <el-tooltip
          v-else-if="viewMap[componentName || ''] !== componentPath"
          :content="
            Object.values(viewMap).includes(componentPath || '')
              ? $t('resource.componentNameChange')
              : $t('resource.componentNonExist')
          "
        >
          <el-icon color="var(--el-color-error)">
            <Warning />
          </el-icon>
        </el-tooltip>
      </template>
    </el-input>
  </div>
</template>
