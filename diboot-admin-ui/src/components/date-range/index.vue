<script setup lang="ts">
import { CircleClose } from '@element-plus/icons-vue'

type ModelValue = [string | undefined, string | undefined]

const props = defineProps<{
  modelValue?: ModelValue
  startPlaceholder?: string
  endPlaceholder?: string
  type?: 'datetimerange' | 'daterange'
}>()

const isDate = (props.type ?? 'daterange') === 'daterange'

const dateRange: { begin?: string; end?: string } = reactive({})

watch(
  () => props.modelValue,
  value => ([dateRange.begin, dateRange.end] = value ? value : [undefined, undefined])
)

const emit = defineEmits<{
  (e: 'update:modelValue', modelValue?: ModelValue): void
  (e: 'change', modelValue?: ModelValue): void
}>()

const change = () => {
  emit('update:modelValue', [dateRange.begin ?? undefined, dateRange.end ?? undefined])
  emit('change', [dateRange.begin ?? undefined, dateRange.end ?? undefined])
}

const disabledBeginDate = (date: Date) => {
  if (dateRange.end) return date > new Date(dateRange.end)
  return false
}

const disabledEndDate = (date: Date) => {
  date.setDate(date.getDate() + 1)
  if (dateRange.begin) return date < new Date(dateRange.begin)
  return false
}

const clearable = () => {
  dateRange.begin = undefined
  dateRange.end = undefined
  change()
}
</script>

<template>
  <div class="date-range">
    <el-date-picker
      v-model="dateRange.begin"
      :placeholder="startPlaceholder"
      style="width: 50%"
      :type="isDate ? 'date' : 'datetime'"
      :value-format="isDate ? 'YYYY-MM-DD' : 'YYYY-MM-DD hh:mm:ss'"
      :disabled-date="disabledBeginDate"
      @change="change"
    />
    <el-icon :size="20" color="var(--el-text-color-placeholder)" @click="clearable">
      <CircleClose class="clearable" />
      <span class="separator">~</span>
    </el-icon>
    <el-date-picker
      v-model="dateRange.end"
      :placeholder="endPlaceholder"
      style="width: 50%"
      :type="isDate ? 'date' : 'datetime'"
      :value-format="isDate ? 'YYYY-MM-DD' : 'YYYY-MM-DD hh:mm:ss'"
      :disabled-date="disabledEndDate"
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
