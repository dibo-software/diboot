<script setup lang="ts" name="FileRecord">
import { fileDownload } from '@/utils/file'
import { Refresh, Search } from '@element-plus/icons-vue'
import Detail from './Detail.vue'
import Form from './Form.vue'

const { queryParam, onSearch, resetFilter, getList, loading, dataList, pagination } = useList<
  FileRecord,
  FileRecord & { createBy: string }
>({
  baseApi: '/file-record'
})

getList()

// 搜索区折叠
const searchState = ref(false)

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

const downloadProgress = ref(0)

const batchDownload = () => {
  fileDownload('/file', selectedKeys.value, percentage => (downloadProgress.value = percentage))
}
</script>

<template>
  <div class="list-page">
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :md="8" :sm="24">
          <el-form-item label="文件名称">
            <el-input v-model="queryParam.fileName" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="创建者">
            <el-select
              v-model="queryParam.createBy"
              remote
              filterable
              :loading="asyncLoading"
              :remote-method="(value: string) => remoteRelatedDataFilter(value, 'userOptions')"
              clearable
              @change="onSearch"
            >
              <el-option
                v-for="item in relatedData.userOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24" style="margin-left: auto">
          <el-form-item>
            <el-button type="primary" @click="onSearch">搜索</el-button>
            <el-button @click="resetFilter">重置</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <el-header>
      <el-space wrap class="list-operation">
        <el-button :loading="downloadProgress > 0 && downloadProgress < 100" @click="batchDownload">批量下载</el-button>
        <el-space>
          <el-button :icon="Refresh" circle @click="getList()" />
          <el-button :icon="Search" circle @click="searchState = !searchState" />
        </el-space>
      </el-space>
    </el-header>
    <el-table
      ref="tableRef"
      v-loading="loading"
      class="list-body"
      :data="dataList"
      stripe
      height="100%"
      @selection-change="(arr: FileRecord[]) => (selectedKeys = arr.map(e => e.uuid))"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column prop="fileName" label="文件名称" />
      <el-table-column prop="fileType" label="文件类型" />
      <el-table-column prop="fileSize" label="文件大小（Byte）" />
      <el-table-column prop="createByName" label="创建者" />
      <el-table-column prop="createTime" label="创建时间" width="165" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'detail'" text bg type="primary" size="small" @click="openDetail(row.uuid)"
              >详情</el-button
            >
            <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row.uuid)"
              >编辑备注</el-button
            >
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
