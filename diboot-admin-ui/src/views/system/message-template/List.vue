<script setup lang="ts" name="MessageTemplate">
import { Search, ArrowDown, Plus } from '@element-plus/icons-vue'
import type { MessageTemplate } from './type'
import Detail from '@/views/system/message-template/Detail.vue'
import Form from './Form.vue'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, remove } = useList<MessageTemplate>({
  baseApi: '/message-template'
})
getList()

const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" :icon="Plus" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input v-model="queryParam.code" clearable :placeholder="$t('messageTemplate.code')" @change="onSearch" />
        <el-input v-model="queryParam.title" clearable :placeholder="$t('messageTemplate.title')" @change="onSearch" />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column prop="code" :label="$t('messageTemplate.code')">
        <template #default="{ row }">
          <el-tag type="info" effect="dark">{{ row.code }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="title" :label="$t('messageTemplate.title')" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ row.title }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="content" :label="$t('messageTemplate.content')" show-overflow-tooltip>
        <template #default="{ row }">
          <span>{{ row.content }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createByName" :label="$t('baseField.createBy')" width="120" />
      <el-table-column prop="createTime" :label="$t('baseField.createTime')" width="165" />
      <el-table-column prop="updateTime" :label="$t('baseField.updateTime')" width="165" />
      <el-table-column :label="$t('operation.label')" width="160" fixed="right">
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
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 15, 20, 30, 50, 100]"
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

<style scoped></style>
