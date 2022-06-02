<script setup name="OrgList" lang="ts">
import { OrgModel } from '@/views/orgUser/org/ModelTypes'

type Props = {
  parentId?: string
}
const props = defineProps<Props>()
withDefaults(props, {
  parentId: '0'
})
const { pageLoader, customQueryParam, queryParam, dataList, pagination } = useList<OrgModel>({
  options: {
    baseApi: '/org'
  }
})
watch(
  () => props.parentId,
  val => {
    if (customQueryParam) {
      ;(customQueryParam as any).parentId = val
    }
    pageLoader.onSearch()
  }
)
</script>
<template />
