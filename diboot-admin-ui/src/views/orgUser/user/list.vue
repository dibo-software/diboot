<script setup lang="ts" name="UserList">
import { Refresh, Search, CircleClose, ArrowDown } from '@element-plus/icons-vue'
import { UserModel } from './type'
import Detail from './detail.vue'
import Form from './form.vue'
import { defineProps, Prop } from 'vue'

type Props = {
  usedVisibleHeight?: number
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

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove } = useListDefault<UserModel>(
  {
    baseApi: '/user'
  }
)
getList()

// 搜索区折叠
const searchState = ref(false)

const detailRef = ref<InstanceType<typeof Detail>>()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const formRef = ref<InstanceType<typeof Form>>()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const loadListByOrgId = (orgId: string) => {
  queryParam.orgId = orgId
  onSearch()
}
</script>
<template>
  <el-space wrap class="list-operation">
    <el-button type="primary" @click="openForm()">新建</el-button>
    <el-space>
      <el-button :icon="Refresh" circle @click="getList()" />
      <el-button :icon="Search" circle @click="searchState = !searchState" />
    </el-space>
  </el-space>
  <el-table
    ref="tableRef"
    v-loading="loading"
    :data="dataList"
    stripe
    :max-height="`calc(100vh - 96px - ${usedVisibleHeight}px)`"
  >
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
    background
    layout="total, sizes, prev, pager, next, jumper"
    :total="pagination.total"
    @size-change="getList()"
    @current-change="getList()"
  />

  <Detail ref="detailRef" />
  <Form ref="formRef" @complete="getList()" />
</template>
