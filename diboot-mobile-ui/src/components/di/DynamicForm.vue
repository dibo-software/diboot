<script setup lang="ts">
import type { ConcreteComponent } from 'vue'

const props = defineProps<{
  baseApi: string
  formName?: string
  disabled?: boolean
  primaryKey?: string
  disabledProps?: string[]
  invisibleProps?: string[]
  form: ConcreteComponent
}>()

const { queryParam, pagination, dataList, onSearch } = useList<Record<string, unknown>>({
  baseApi: props.baseApi
})
pagination.pageSize = 999

defineExpose({
  addCondition: (key: string, value: unknown, refresh = false) => {
    queryParam[key] = value as any
    if (refresh) onSearch()
  },
  validate: async () => {
    for (let item of dataList) {
      await (item._el as any).validate?.()
    }
  },
  getData: async () => {
    const list: Record<string, unknown>[] = []
    await Promise.all(dataList.map(async item => list.push(await (item._el as any).getData())))
    return list
  },
  reset: () => (dataList.length = 0)
})

const _toRaw = toRaw
const activeNames = ref<string[]>([])
</script>

<template>
  <div>
    <van-collapse v-model="activeNames">
      <van-collapse-item
        :key="`${item[primaryKey || 'id'] ?? item._uid}`"
        :name="`${item[primaryKey || 'id'] ?? item._uid}`"
        :title="`${formName ?? ''} ${index + 1}、`"
        :lazy-render="false"
        v-for="(item, index) in dataList"
      >
        <component
          :is="form as ConcreteComponent"
          :ref="
            (el?: any) => el && !item._el && !!(item._el = el) && el.init(item[primaryKey || 'id'], true, _toRaw(item))
          "
          inside
          :disabled="disabled"
          :disabled-props="disabledProps"
          :invisible-props="invisibleProps"
        />
        <div style="display: flex; justify-content: space-between">
          <van-button
            v-if="!disabled && !invisibleProps?.includes('REMOVE_FLAG__')"
            icon="delete-o"
            type="danger"
            size="small"
            plain
            style="width: 33%"
            @click="dataList.splice(index, 1)"
          >
            {{ $t('operation.delete') }}
          </van-button>
          <van-button
            v-if="!disabled && !invisibleProps?.includes('APPEND_FLAG__')"
            icon="description"
            type="success"
            size="small"
            plain
            style="width: 66%"
            @click="
              async () =>
                dataList.splice(index + 1, 0, {
                  ...(await (item._el as any).getData()),
                  [props.primaryKey || 'id']: void 0,
                  _uid: +new Date(),
                  _el: void 0
                })
            "
            :style="{ marginBottom: '8px' }"
          >
            {{ $t('operation.copy') }}
          </van-button>
        </div>
      </van-collapse-item>
    </van-collapse>
    <van-button
      v-if="!disabled && !invisibleProps?.includes('APPEND_FLAG__')"
      plain
      icon="plus"
      size="small"
      type="primary"
      style="width: calc(100% - 20px); display: block; margin: auto"
      @click="dataList.push({ _uid: +new Date() })"
    >
      {{ $t('operation.create') }}
    </van-button>
  </div>
</template>
