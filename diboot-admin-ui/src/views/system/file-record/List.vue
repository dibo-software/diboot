<script setup lang="ts" name="FileRecord">
import { Search } from '@element-plus/icons-vue'
import Detail from './Detail.vue'
import Form from './Form.vue'

const { queryParam, onSearch, resetFilter, getList, loading, dataList, pagination } = useList<
  FileRecord,
  FileRecord & { createBy: string }
>({
  baseApi: '/file-record'
})

getList()

const { relatedData, asyncLoading, remoteRelatedDataFilter } = useOption({
  asyncLoad: { userOptions: { type: 'IamUser', label: 'realname' } }
})

// 选中的数据 Id 集合
const selectedKeys = ref<string[]>([])

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}
const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input v-model="queryParam.fileName" clearable :placeholder="$t('fileRecord.fileName')" @change="onSearch" />
        <el-select
          v-model="queryParam.createBy"
          remote
          filterable
          :loading="asyncLoading"
          :remote-method="(value: string) => remoteRelatedDataFilter('userOptions', value)"
          clearable
          :placeholder="$t('baseField.createBy')"
          @change="onSearch"
        >
          <el-option
            v-for="item in relatedData.userOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table
      ref="tableRef"
      v-loading="loading"
      class="list-body"
      :data="dataList"
      stripe
      height="100%"
      @selection-change="(arr: FileRecord[]) => (selectedKeys = arr.map(e => e.id))"
    >
      <el-table-column prop="fileName" :label="$t('fileRecord.fileName')" show-overflow-tooltip />
      <el-table-column prop="fileType" :label="$t('fileRecord.fileType')" width="140" />
      <el-table-column prop="fileSizeLabel" :label="$t('fileRecord.fileSize')" width="140" />
      <el-table-column prop="createByName" :label="$t('baseField.createBy')" width="140" />
      <el-table-column prop="createTime" :label="$t('baseField.createTime')" width="185" />
      <el-table-column :label="$t('operation.label')" width="160" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'detail'" text bg type="primary" size="small" @click="openDetail(row.id)"
              >{{ $t('operation.detail') }}
            </el-button>
            <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row.id)"
              >{{ $t('fileRecord.editDescription') }}
            </el-button>
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
