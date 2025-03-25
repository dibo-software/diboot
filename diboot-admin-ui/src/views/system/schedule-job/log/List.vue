<script setup lang="ts">
import { ArrowUp, ArrowDown, Search, Delete } from '@element-plus/icons-vue'
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
  <el-drawer v-model="visible" :title="$t('scheduleJobLog.title')" size="800px">
    <div class="list-page">
      <el-header>
        <el-space wrap class="list-operation">
          <el-button v-has-permission="'logDelete'" :icon="Delete" @click="batchRemove(selectedKeys)">{{
            $t('operation.batchDelete')
          }}</el-button>
          <el-space>
            <el-select
              v-model="queryParam.runStatus"
              :label="$t('scheduleJobLog.runStatus')"
              :placeholder="$t('scheduleJobLog.runStatus')"
              clearable
              @change="onSearch"
            >
              <el-option :label="$t('scheduleJobLog.success')" value="S" />
              <el-option :label="$t('scheduleJobLog.fail')" value="F" />
            </el-select>
            <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
            <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
            <el-button
              :icon="searchState ? ArrowUp : ArrowDown"
              :title="searchState ? $t('searchState.up') : $t('searchState.down')"
              @click="searchState = !searchState"
            />
          </el-space>
        </el-space>
      </el-header>
      <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
        <el-row :gutter="18">
          <el-col :md="12" :sm="24">
            <el-form-item :label="$t('scheduleJobLog.startTimeAlias')">
              <date-range
                :model-value="dateRangeQuery.startTime as [string, string]"
                @update:model-value="dateRangeQuery.startTime = $event as [string, string]"
                @change="onSearch"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="dataList"
        height="100%"
        stripe
        style="border-top: 1px solid var(--el-border-color-lighter)"
        @selection-change="(arr: ScheduleJobLog[]) => (selectedKeys = arr.map(e => e.id))"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="startTime" :label="$t('scheduleJobLog.startTimeAlias')" width="160" />
        <el-table-column prop="endTime" :label="$t('scheduleJobLog.endTime')" width="160" />
        <el-table-column prop="elapsedSeconds" :label="$t('scheduleJobLog.elapsedSeconds')" align="right" width="90" />
        <el-table-column prop="runStatus" :label="$t('scheduleJobLog.runStatus')">
          <template #default="{ row }">
            <el-tag v-if="row.runStatus === 'S'">{{ $t('scheduleJobLog.success') }}</el-tag>
            <el-tag v-else type="danger">{{ $t('scheduleJobLog.fail') }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('operation.label')" width="130" fixed="right">
          <template #default="{ row }">
            <el-button text bg type="primary" size="small" @click="openDetail(row.id)"
              >{{ $t('title.detail') }}
            </el-button>
            <el-button v-has-permission="'logDelete'" text bg size="small" @click="remove(row.id)">
              {{ $t('operation.delete') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-if="pagination.total"
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 15, 20, 30, 50, 100]"
        size="small"
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
