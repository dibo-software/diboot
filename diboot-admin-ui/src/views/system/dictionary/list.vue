<script setup lang="ts" name="DictionaryList">
interface Dictionary {
  type: string
  itemName: string
  itemValue: string
  description: string
  children: Dictionary[]
  createTime: string
}
const customParam = ref({})
let searchParam = ref({})
let dataList = ref<Dictionary[]>([])

const buildQueryParam = async () => {
  const queryParam = {}
  _.merge(queryParam, customParam)
  _.merge(queryParam, searchParam)
  return queryParam
}
const clearSearchParam = () => {
  searchParam.value = {}
}
const queryDataList = async () => {
  const queryParam = await buildQueryParam()
  try {
    const res = await api.get<Dictionary[]>('/dictionary/list', queryParam)
    if (res.code === 0) {
      dataList.value = res.data || []
    }
  } catch (e) {
    console.error(e)
  }
}
const onSearch = async () => {
  await buildQueryParam()
  await queryDataList()
}
const onReset = async () => {
  clearSearchParam()
  await queryDataList()
}
onMounted(() => {
  onSearch()
})
</script>
<template>
  <div class="list-container">
    <div class="search-container">
      <el-form :inline="true" :model="searchParam" class="demo-form-inline">
        <el-form-item label="菜单名称">
          <el-input v-model="searchParam.itemName" placeholder="类型名称" />
        </el-form-item>
        <el-form-item label="菜单编码">
          <el-input v-model="searchParam.itemValue" placeholder="类型编码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-container">
      <el-table :data="dataList" row-key="id" default-expand-all>
        <el-table-column prop="itemName" label="类型名称" />
        <el-table-column prop="itemValue" label="类型编码" />
        <el-table-column prop="description" label="备注" />
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
    </div>
  </div>
</template>
