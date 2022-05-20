<script setup lang="ts" name="DictionaryList">
interface Dictionary {
  id: string
  type: string
  itemName: string
  itemValue: string
  description: string
  children: Dictionary[]
  createTime: string
}

const { pageLoader, queryParam, dataList } = useList<Dictionary>({
  options: {
    baseApi: '/dictionary'
  }
})
const onReset = () => {
  pageLoader.onReset()
}
</script>
<template>
  <div class="list-container">
    <div class="search-container">
      <el-form :inline="true" :model="queryParam" class="demo-form-inline">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParam.itemName" placeholder="类型名称" />
        </el-form-item>
        <el-form-item label="菜单编码">
          <el-input v-model="queryParam.itemValue" placeholder="类型编码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="pageLoader.onDebounceSearch()">查询</el-button>
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
