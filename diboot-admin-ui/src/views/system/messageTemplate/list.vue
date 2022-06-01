<script setup lang="ts" name="MessageTemplate">
import { Refresh, Search, CircleClose, ArrowDown } from '@element-plus/icons-vue'
import type { MessageTemplate } from './type'
import Detail from '@/views/system/messageTemplate/detail.vue'
import Form from './form.vue'

defineProps<{ usedVisibleHeight?: number }>()

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove } =
  useListDefault<MessageTemplate>({
    baseApi: '/messageTemplate'
  })
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
</script>

<template>
  <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
    <el-row :gutter="18">
      <el-col :md="6" :sm="12">
        <el-form-item label="模版编码">
          <el-input v-model="queryParam.code" clearable placeholder="" @change="onSearch" />
        </el-form-item>
      </el-col>
      <el-col :md="6" :sm="12">
        <el-form-item label="模版标题">
          <el-input v-model="queryParam.title" clearable placeholder="" @change="onSearch" />
        </el-form-item>
      </el-col>
      <el-col :md="6" :sm="12">
        <el-form-item label="创建时间">
          <el-date-picker
            v-model="queryParam.createTime"
            clearable
            type="date"
            value-format="yyyy-MM-dd"
            @change="onSearch"
          />
        </el-form-item>
      </el-col>
      <el-col :md="6" :sm="12" style="margin-left: auto">
        <el-form-item>
          <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
          <el-button :icon="CircleClose" title="重置搜索条件" @click="resetFilter" />
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
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
    <el-table-column prop="code" label="模版编码">
      <template #default="{ row }">
        <el-tag type="info">{{ row.code }}</el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="title" label="模版标题" show-overflow-tooltip>
      <template #default="{ row }">
        <span>{{ row.title }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="content" label="模版内容" show-overflow-tooltip>
      <template #default="{ row }">
        <span>{{ row.content }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="createByName" label="创建人" width="120" />
    <el-table-column prop="createTime" label="创建时间" />
    <el-table-column prop="updateTime" label="更新时间" />
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

<style scoped></style>
