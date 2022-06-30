<script setup lang="ts" name="IamResourcePermissionList">
import { useLoadRelatedData } from '@/utils/relatedData'
// script
const queryParams = reactive({})
const onSubmit = () => {
  console.log(queryParams)
}
const relatedData = useLoadRelatedData([{}], '/iamResource')

type User = {
  id: number
  date: string
  name: string
  hasChildren?: boolean
  children?: User[]
}
const tableData: User[] = [
  {
    id: 1,
    date: '2016-05-02',
    name: 'wangxiaohu'
  },
  {
    id: 2,
    date: '2016-05-04',
    name: 'wangxiaohu'
  },
  {
    id: 3,
    date: '2016-05-01',
    name: 'wangxiaohu',
    children: [
      {
        id: 31,
        date: '2016-05-01',
        name: 'wangxiaohu'
      },
      {
        id: 32,
        date: '2016-05-01',
        name: 'wangxiaohu'
      }
    ]
  },
  {
    id: 4,
    date: '2016-05-03',
    name: 'wangxiaohu'
  }
]
</script>
<template>
  <div class="list-container">
    <div class="search-container">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item>
          <el-input v-model="queryParams.displayName" placeholder="菜单名称" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="queryParams.resourceCode" placeholder="菜单编码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">查询</el-button>
          <el-button @click="onSubmit">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="table-container">
      <el-table :data="tableData" row-key="id" default-expand-all>
        <el-table-column prop="date" label="菜单名称" />
        <el-table-column prop="name" label="菜单编码" />
        <el-table-column prop="name" label="页面按钮权限" />
        <el-table-column prop="name" label="创建时间" />
        <el-table-column prop="date" label="操作" />
      </el-table>
    </div>
  </div>
</template>
<style scoped lang="scss">
.list-container {
  background-color: #fff;
  padding: 12px;
}
</style>
