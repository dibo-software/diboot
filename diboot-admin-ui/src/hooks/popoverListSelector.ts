import { ElMessage } from 'element-plus'
import type { ComputedRef, Ref } from 'vue'

export interface PopoverListSelectorOption<T> {
  baseApi: string
  listByIdsApi?: string
  modelValue: string
  keyName?: string
  multi?: boolean
  labelKey: string
  valueKey: string
  optionsValue: ComputedRef<string[] | string>
}

export default <T>(option: PopoverListSelectorOption<T>) => {
  const selectOptions = ref<Record<string, string>[]>([])
  const selectedRows = ref<T[]>([]) as Ref<T[]>

  const { optionsValue, labelKey, valueKey } = option

  const selectedKeys = computed<string[]>(() => {
    const { multi } = option
    if (multi) {
      return optionsValue.value as any
    } else {
      return [optionsValue.value]
    }
  })

  // 通过监听值的变化，来自动加载用于回显的选项列表数据
  watch(
    () => option.modelValue,
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
    const res = await api.get<T[]>('/role/listByIds', { ids })
    if (res.code === 0) {
      const { data } = res
      if (data && data.length > 0) {
        selectedRows.value = data
        selectOptions.value = data.map(
          (item: T) =>
            ({
              label: `${item[labelKey as keyof T]}`,
              value: `${item[valueKey as keyof T]}`
            } as Record<string, string>)
        )
      }
    } else {
      ElMessage.warning('未加载到选项初始数据')
    }
  }
}
