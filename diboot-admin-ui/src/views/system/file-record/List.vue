<script setup lang="ts" name="FileRecord">
import { Search, ArrowDown, ArrowUp } from '@element-plus/icons-vue'
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
</script>

<template>
  <div class="list-page">
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :lg="6" :sm="12">
          <el-form-item label="文件名称">
            <el-input v-model="queryParam.fileName" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :lg="6" :sm="12">
          <el-form-item label="创建者">
            <el-select
              v-model="queryParam.createBy"
              remote
              filterable
              :loading="asyncLoading"
              :remote-method="(value: string) => remoteRelatedDataFilter('userOptions', value)"
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
      </el-row>
    </el-form>

    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input
          v-show="!searchState"
          v-model="queryParam.fileName"
          clearable
          placeholder="文件名称"
          @change="onSearch"
        />
        <el-button :icon="Search" type="primary" @click="onSearch">查询</el-button>
        <el-button title="重置搜索条件" @click="resetFilter">重置</el-button>
        <el-button
          :icon="searchState ? ArrowUp : ArrowDown"
          :title="searchState ? '收起' : '展开'"
          @click="searchState = !searchState"
        />
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
      <el-table-column prop="fileName" label="文件名称" show-overflow-tooltip />
      <el-table-column prop="fileType" label="文件类型" width="140" />
      <el-table-column prop="fileSizeLabel" label="文件大小" width="140" />
      <el-table-column prop="createByName" label="创建者" width="140" />
      <el-table-column prop="createTime" label="创建时间" width="185" />
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-space>
            <el-button v-has-permission="'detail'" text bg type="primary" size="small" @click="openDetail(row.id)"
              >详情
            </el-button>
            <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row.id)"
              >编辑备注
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
