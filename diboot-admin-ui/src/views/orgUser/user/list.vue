<script setup lang="ts" name="UserList">
import { Refresh, Search, ArrowDown } from '@element-plus/icons-vue'
import type { UserModel } from './type'
import Detail from './detail.vue'
import Form from './form.vue'

type Props = {
  orgId?: string
}
const props = withDefaults(defineProps<Props>(), {
  orgId: '0'
})
watch(
  () => props.orgId,
  val => {
    loadListByOrgId(val)
  }
)

interface UserSearch extends UserModel {
  keywords?: string
}
const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove } = useListDefault<
  UserModel,
  UserSearch
>({
  baseApi: '/user'
})
getList()
const searchVal = ref('')
const onSearchValChanged = (val: string) => {
  queryParam.keywords = val
  onSearch()
}

const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const loadListByOrgId = (orgId: string) => {
  queryParam.orgId = orgId
  onSearch()
}
</script>
<template>
  <div class="list-page">
    <el-header>
      <el-space wrap class="list-operation">
        <el-button type="primary" @click="openForm()">新建</el-button>
        <el-space>
          <el-input
            v-model="searchVal"
            class="search-input"
            placeholder="编码/名称"
            clearable
            :suffix-icon="Search"
            @change="onSearchValChanged"
          />
          <el-button :icon="Refresh" circle @click="getList()" />
        </el-space>
      </el-space>
    </el-header>
    <el-table ref="tableRef" v-loading="loading" row-key="id" :data="dataList" stripe height="100%">
      <el-table-column prop="realname" label="姓名" />
      <el-table-column prop="userNum" label="编号" />
      <el-table-column prop="genderLabel" label="性别" />
      <el-table-column prop="mobilePhone" label="电话" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-space>
            <el-button text bg type="primary" size="small" @click="openDetail(row.id)">详情</el-button>
            <el-dropdown>
              <el-button text bg type="primary" size="small">
                更多
                <el-icon :size="16" style="margin-left: 5px">
                  <arrow-down />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="openForm(row.id)">编辑</el-dropdown-item>
                  <el-dropdown-item @click="remove(row.id)">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </el-space>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="pagination.total"
      v-model:currentPage="pagination.current"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 30, 50, 100]"
      small
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="getList()"
      @current-change="getList()"
    />
  </div>
  <Detail ref="detailRef" />
  <Form ref="formRef" @complete="getList()" />
</template>
<style lang="scss" scoped>
.el-header {
  height: auto;
  padding: 10px;
  border-bottom: 1px solid var(--el-border-color);
}
.list-operation {
  margin-bottom: 0;
  padding: 0;
}
</style>
