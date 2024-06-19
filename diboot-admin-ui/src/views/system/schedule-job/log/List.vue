<script setup lang="ts">
import { Refresh, Search } from '@element-plus/icons-vue'
import type { ScheduleJobLog } from '../type'
import Detail from './Detail.vue'

const visible = ref(false)

const {
  queryParam,
  dateRangeQuery,
  resetFilter,
  loading,
  getList,
  onSearch,
  dataList,
  pagination,
  remove,
  batchRemove
} = useList<ScheduleJobLog>({
  baseApi: '/schedule-job/log'
})

watch(visible, value => !value && dataList.slice(0))

defineExpose({
  open: (id: string) => {
    if (queryParam.jobId !== id) {
      resetFilter()
      queryParam.jobId = id
      onSearch()
    } else {
      getList()
    }
    visible.value = true
  }
})
// 搜索区折叠
const searchState = ref(false)

// 选中的数据 Id 集合
const selectedKeys = ref<string[]>([])

const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}
</script>

<template>
  <el-drawer v-model="visible" title="日志" size="850px">
    <div class="list-page">
      <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
        <el-row :gutter="18">
          <el-col :md="12" :sm="24">
            <el-form-item label="执行结果">
              <el-select v-model="queryParam.runStatus" clearable @change="onSearch">
                <el-option label="成功" value="S" />
                <el-option label="失败" value="F" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="触发方式">
              <el-select v-model="queryParam.triggerMode" clearable @change="onSearch">
                <el-option label="自动" value="AUTO" />
                <el-option label="手动" value="MANUAL" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24">
            <el-form-item label="执行时间">
              <date-range
                :model-value="dateRangeQuery.startTime as [string, string]"
                @update:model-value="dateRangeQuery.startTime = $event as [string, string]"
                @change="onSearch"
              />
            </el-form-item>
          </el-col>
          <el-col :md="12" :sm="24" style="margin-left: auto">
            <el-form-item>
              <el-button type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
              <el-button @click="resetFilter">{{ $t('operation.reset') }}</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <el-header>
        <el-space wrap class="list-operation">
          <el-button v-has-permission="'logDelete'" @click="batchRemove(selectedKeys)">批量删除</el-button>
          <el-space>
            <el-button :icon="Refresh" circle @click="getList()" />
            <el-button :icon="Search" circle @click="searchState = !searchState" />
          </el-space>
        </el-space>
      </el-header>
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="dataList"
        height="100%"
        @selection-change="(arr: ScheduleJobLog[]) => (selectedKeys = arr.map(e => e.id))"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="startTime" label="执行时间" align="center" width="165" />
        <el-table-column prop="endTime" label="结束时间" align="center" width="165" />
        <el-table-column prop="elapsedSeconds" label="耗时（s）" align="right" width="100" />
        <el-table-column prop="triggerModeLabel" align="center" label="触发方式" />
        <el-table-column prop="runStatus" align="center" label="执行结果">
          <template #default="{ row }">
            <el-tag v-if="row.runStatus === 'S'">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('operation.label')" align="center" width="130" fixed="right">
          <template #default="{ row }">
            <el-button text bg type="primary" size="small" @click="openDetail(row.id)"
              >{{ $t('title.detail') }}
            </el-button>
            <el-button v-has-permission="'logDelete'" text bg type="primary" size="small" @click="remove(row.id)">
              删除
            </el-button>
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
    </div>
  </el-drawer>

  <Detail ref="detailRef" />
</template>

<style scoped></style>
