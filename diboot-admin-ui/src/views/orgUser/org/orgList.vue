<script setup name="OrgList" lang="ts">
import { OrgModel } from '@/views/orgUser/org/type'

type Props = {
  parentId?: string
}
const props = withDefaults(defineProps<Props>(), {
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
      ;(customQueryParam.value as any).parentId = val
    }
    pageLoader.onSearch()
  }
)
</script>
<template>
  <div style="width: 100%">
    <el-table row-key="id" :tree-props="{ children: 'children__' }" :data="dataList" stripe>
      <el-table-column prop="name" label="全称" />
      <el-table-column prop="shortName" label="简称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="createTime" label="创建时间" />
    </el-table>
    <el-pagination
      v-if="pagination.total"
      v-model:currentPage="pagination.current"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 30, 50, 100]"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="pageLoader.getList()"
      @current-change="pageLoader.getList()"
    />
  </div>
</template>
