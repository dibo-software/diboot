<script setup lang="ts">
import type { PickerFieldNames, PickerOption } from 'vant'
import { Radio, Checkbox } from 'vant'
import type { Component } from 'vue'

defineProps<{ column: PickerOption; columnsFieldNames?: PickerFieldNames; square?: boolean }>()
</script>

<template>
  <div>
    <van-cell :title="column[columnsFieldNames?.text ?? 'text']">
      <template #title>
        <component
          :is="(square ? Checkbox : Radio) as Component"
          :name="column[columnsFieldNames?.value ?? 'value']"
          :shape="square ? 'square' : undefined"
        >
          <div v-if="typeof column.ext === 'string'" class="option">
            {{ column[columnsFieldNames?.text ?? 'text'] }}
            <span class="ext">（{{ column.ext }}）</span>
          </div>
          <template v-else>
            {{ column[columnsFieldNames?.text ?? 'text'] }}
          </template>
        </component>
      </template>
    </van-cell>
    <Column
      v-for="item in column?.children"
      :key="item[columnsFieldNames?.value ?? 'value']"
      :column="item as PickerOption"
      :columns-field-names="columnsFieldNames"
      :square="square"
      style="margin-left: 25px"
    />
  </div>
</template>

<style scoped lang="scss">
:deep(.van-radio__label),
:deep(.van-checkbox__label) {
  width: 100%;

  .option {
    display: flex;
    justify-content: space-between;

    .ext {
      font-size: var(--van-font-size-sm);
      color: var(--van-text-color-2);
    }
  }
}
</style>
