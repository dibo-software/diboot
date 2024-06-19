<script setup lang="ts">
import { CircleClose } from '@element-plus/icons-vue'
type ModelValue = [number | undefined, number | undefined]
const props = withDefaults(
  defineProps<{
    modelValue?: ModelValue
    startPlaceholder?: string
    endPlaceholder?: string
    min?: number
    max?: number
    // 精度（未指定则为整数）
    precision?: number
    controls?: false | 'right'
  }>(),
  {
    modelValue: undefined,
    min: undefined,
    max: undefined,
    precision: undefined,
    controls: undefined
  }
)

const dataRange: { begin?: number; end?: number } = reactive({})

watch(
  () => props.modelValue,
  value => ([dataRange.begin, dataRange.end] = value ? value : [undefined, undefined])
)

const emit = defineEmits<{
  (e: 'update:modelValue', modelValue?: ModelValue): void
  (e: 'change', modelValue?: ModelValue): void
}>()

const change = () => {
  emit('update:modelValue', [dataRange.begin ?? undefined, dataRange.end ?? undefined])
  emit('change', [dataRange.begin ?? undefined, dataRange.end ?? undefined])
}

const clearable = () => {
  dataRange.begin = undefined
  dataRange.end = undefined
  change()
}
</script>

<template>
  <div class="date-range">
    <el-input-number
      v-model="dataRange.begin"
      :placeholder="startPlaceholder || $t('components.numberRange.startPlaceholder')"
      :min="min"
      :max="dataRange.end ?? max"
      :precision="precision"
      :step-strictly="!precision"
      :controls="controls === false ? false : undefined"
      :controls-position="controls === 'right' ? 'right' : ''"
      style="width: 50%"
      @change="change"
    />
    <el-icon :size="20" color="var(--el-text-color-placeholder)" @click="clearable">
      <CircleClose class="clearable" />
      <span class="separator">~</span>
    </el-icon>
    <el-input-number
      v-model="dataRange.end"
      :placeholder="endPlaceholder || $t('components.numberRange.endPlaceholder')"
      :min="dataRange.begin ?? min"
      :max="max"
      :precision="precision"
      :step-strictly="!precision"
      :controls="controls === false ? false : undefined"
      :controls-position="controls === 'right' ? 'right' : ''"
      style="width: 50%"
      @change="change"
    />
  </div>
</template>

<style scoped lang="scss">
.date-range {
  display: inline-flex;
  width: 100%;

  .el-icon {
    width: 30px;
    height: inherit;
    display: inline-flex;
    justify-content: center;

    .clearable {
      display: none;
    }
    .separator {
      zoom: 0.8;
    }

    &:hover {
      .clearable {
        display: inline;
      }

      .separator {
        display: none;
      }
    }
  }
}
</style>
