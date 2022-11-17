<script setup lang="ts" name="Position">
import { Refresh, Search, ArrowDown } from '@element-plus/icons-vue'
import type { Position } from './type'
import Detail from './Detail.vue'
import Form from './Form.vue'
interface PositionSearch extends Position {
  keywords?: string
}

const { queryParam, onSearch, getList, loading, dataList, pagination, remove } = useList<Position, PositionSearch>({
  baseApi: '/iam/position'
})

getList()

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}
const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')
</script>

<template>
  <div class="list-page">
    <el-header>
      <el-space wrap class="list-operation">
        <el-button v-has-permission="'create'" type="primary" @click="openForm()">
          {{ $t('operation.create') }}
        </el-button>
        <el-space>
          <el-input
            v-model="queryParam.keywords"
            class="search-input"
            placeholder="编码/名称"
            clearable
            :suffix-icon="Search"
            @keyup.enter="onSearch"
          />
          <el-button :icon="Refresh" circle @click="getList()" />
        </el-space>
      </el-space>
    </el-header>
    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="gradeName" label="职级" />
      <el-table-column prop="dataPermissionTypeLabel" label="数据权限" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'detail'" text bg type="primary" size="small" @click="openDetail(row.id)">
              {{ $t('operation.detail') }}
            </el-button>
            <el-dropdown v-has-permission="['update', 'delete']">
              <el-button text bg type="primary" size="small">
                {{ $t('operation.more') }}
                <el-icon :size="16" style="margin-left: 5px">
                  <arrow-down />
                </el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item v-if="updatePermission" @click="openForm(row.id)">
                    {{ $t('operation.update') }}
                  </el-dropdown-item>
                  <el-dropdown-item v-if="deletePermission" @click="remove(row.id)">
                    {{ $t('operation.delete') }}
                  </el-dropdown-item>
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

    <Detail ref="detailRef" />
    <Form ref="formRef" @complete="getList()" />
  </div>
</template>

<style lang="scss" scoped></style>
