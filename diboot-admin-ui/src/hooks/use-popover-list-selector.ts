import { ElMessage } from 'element-plus'
import type { Ref } from 'vue'
import type { WritableComputedRef } from 'vue'

export interface PopoverListSelectorOption<T> {
  baseApi: string
  listByIdsApi?: string
  keyName?: string
  multi?: boolean
  labelKey: string
  valueKey: string
  optionsValue: WritableComputedRef<string[] | string>
}

export default <T>(option: PopoverListSelectorOption<T>) => {
  const selectOptions = ref<Record<string, string>[]>([])
  const selectedRows = ref<T[]>([]) as Ref<T[]>

  const { baseApi, listByIdsApi, optionsValue, labelKey, valueKey } = option

  const selectedKeys = computed<string[]>(() => {
    const { multi } = option
    if (multi) {
      return optionsValue.value as any
    } else {
      return [optionsValue.value]
    }
  })

  const onSelect = (rows: T[]) => {
    const { multi } = option
    const labelValueList = rows.map(item => {
      return {
        label: `${item[labelKey as keyof T]}`,
        value: `${item[valueKey as keyof T]}`
      } as Record<string, string>
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
    const listByIdsUrl = listByIdsApi ? listByIdsApi : `${baseApi}/listByIds`
    const res = await api.get<T[]>(listByIdsUrl, { ids })
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

  return {
    selectOptions,
    selectedRows,
    selectedKeys,
    onSelect,
    loadInitOptions
  }
}
