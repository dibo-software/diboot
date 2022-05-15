<script setup lang="ts" name="RoleList">
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'

defineProps<{ usedVisibleHeight?: number }>()

interface Role {
  id: string
  name: string
  code: string
  description: string
  createTime: string
  updateTime: string
}

const {
  queryParam,
  dateRangeQuery,
  onSearch,
  resetFilter,
  getList,
  loading,
  dataList,
  pagination,
  remove,
  batchRemove
} = useList<Role>({ baseApi: '/role' })

getList()

// 搜索区折叠
const advanced = ref(false)

// 选中的数据 Id 集合
const multipleSelectionIds = ref<string[]>([])

const openDetail = (id: string) => {
  ElMessage({
    message: 'open:Detail ' + id,
    grouping: true,
    type: 'success'
  })
}
const openEdit = (id: any) => {
  ElMessage({
    message: 'open:Edit ' + id,
    grouping: true,
    type: 'warning'
  })
}
</script>

<template>
  <el-form label-width="80px" @submit.prevent>
    <el-row :gutter="18">
      <el-col :md="8" :sm="24">
        <el-form-item label="名称">
          <el-input v-model="queryParam.name" @keyup.enter="onSearch" />
        </el-form-item>
      </el-col>
      <el-col :md="8" :sm="24">
        <el-form-item label="编码">
          <el-input v-model="queryParam.code" @keyup.enter="onSearch" />
        </el-form-item>
      </el-col>
      <template v-if="advanced">
        <el-col :md="8" :sm="24">
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="dateRangeQuery.createTime"
              type="daterange"
              value-format="YYYY-MM-DD"
              @change="onSearch"
            />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="更新时间">
            <el-date-picker
              v-model="queryParam.updateTime"
              type="date"
              value-format="YYYY-MM-DD"
              style="width: 100%"
              @change="onSearch"
            />
          </el-form-item>
        </el-col>
      </template>
      <el-col :md="8" :sm="24" style="margin-left: auto">
        <el-form-item>
          <el-button type="primary" @click="onSearch">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="primary" text @click="advanced = !advanced">
            {{ advanced ? '收起' : '展开' }}
            <el-icon :size="18" style="margin-left: 5px">
              <component :is="advanced ? ArrowUp : ArrowDown" />
            </el-icon>
          </el-button>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
  <el-space>
    <el-button type="primary">新建</el-button>
    <el-button @click="batchRemove(multipleSelectionIds)">批量删除</el-button>
    <el-button>导入</el-button>
    <el-button>导出</el-button>
  </el-space>
  <el-table
    ref="tableRef"
    v-loading="loading"
    :data="dataList"
    :max-height="`calc(100vh - 120px - ${usedVisibleHeight}px)`"
    @selection-change="arr => (multipleSelectionIds = arr.map((e: Role) => e.id))"
  >
    <el-table-column type="selection" width="55" />
    <el-table-column prop="name" label="名称" />
    <el-table-column prop="code" label="编码" />
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
                <el-dropdown-item @click="openEdit(row.id)">编辑</el-dropdown-item>
                <el-dropdown-item @click="remove(row.id)">删除</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </el-space>
      </template>
    </el-table-column>
  </el-table>
  <el-pagination
    v-show="pagination.total > 0"
    v-model:currentPage="pagination.current"
    v-model:page-size="pagination.pageSize"
    style="position: relative; top: 10px; margin-bottom: 10px; justify-content: flex-end"
    :page-sizes="[10, 20, 30, 50, 100]"
    background
    layout="total, sizes, prev, pager, next, jumper"
    :total="pagination.total"
  />
</template>

<style scoped></style>
