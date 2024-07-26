<script setup lang="ts" name="LoginTrace">
import { Search } from '@element-plus/icons-vue'
import type { LoginTrace } from './type'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()

const { queryParam, dateRangeQuery, loading, dataList, pagination, getList, onSearch, resetFilter } =
  useList<LoginTrace>({
    baseApi: '/iam/login-trace'
  })

getList()

// 强制退出
const forceLogout = (id: string) => {
  ElMessageBox.confirm(i18n.t('loginTrace.formLogoutMessage.confirmContent'), 'Warning', {
    type: 'warning'
  }).then(async () => {
    const res = await api.post(`/iam/login-trace/force-logout/${id}`)
    if (res.code === 0) {
      ElMessage.success(i18n.t('loginTrace.formLogoutMessage.success'))
    } else {
      console.log('强制退出失败', res.msg)
      ElMessage.warning(i18n.t('loginTrace.formLogoutMessage.failed'))
    }
  })
}
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-space>
        <el-input
          v-model="queryParam.authAccount"
          :placeholder="$t('loginTrace.authAccount')"
          clearable
          @change="onSearch"
        />
        <el-select
          v-model="queryParam.isSuccess"
          :placeholder="$t('loginTrace.placeholder.successStatus')"
          clearable
          @change="onSearch"
        >
          <el-option :label="$t('loginTrace.successStatus.yes')" :value="true" />
          <el-option :label="$t('loginTrace.successStatus.yes')" :value="false" />
        </el-select>
        <date-range
          :start-placeholder="$t('loginTrace.placeholder.start')"
          :end-placeholder="$t('loginTrace.placeholder.end')"
          :model-value="dateRangeQuery.createTime as [string, string]"
          @update:model-value="dateRangeQuery.createTime = $event as [string, string]"
          @change="onSearch"
        />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" stripe height="100%">
      <el-table-column :label="$t('loginTrace.userTypeId')" width="260">
        <template #default="{ row }">
          <span>{{ row.userType }}:{{ row.userId }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="authAccount" :label="$t('loginTrace.authAccount')" />
      <el-table-column prop="ipAddress" :label="$t('loginTrace.ipAddress')" />
      <el-table-column prop="authType" :label="$t('loginTrace.authType')" />
      <el-table-column prop="osInfo" :label="$t('loginTrace.osInfo')" />
      <el-table-column prop="browserInfo" :label="$t('loginTrace.browserInfo')" />
      <el-table-column prop="success" :label="$t('loginTrace.success')">
        <template #default="{ row }">
          <el-tag v-if="row.isSuccess">{{ $t('loginTrace.successStatus.yes') }}</el-tag>
          <el-tag v-else type="danger">{{ $t('loginTrace.successStatus.no') }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="loginStatus" :label="$t('loginTrace.onlineStatusLabel')">
        <template #default="{ row }">
          <el-tag v-if="row.onlineStatus === 'ONLINE'">{{ $t('loginTrace.onlineStatus.online') }}</el-tag>
          <el-tag v-else type="danger">{{ $t(`loginTrace.onlineStatus.${row.onlineStatus?.toLowerCase()}`) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" :label="$t('loginTrace.createTime')" />
      <el-table-column prop="logoutTime" :label="$t('loginTrace.logoutTime')" />
      <el-table-column :label="$t('operation.label')" width="160" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.onlineStatus === 'ONLINE'"
            text
            bg
            type="danger"
            size="small"
            @click="forceLogout(row.id)"
          >
            {{ $t('loginTrace.forceLogout') }}
          </el-button>
          <span v-else>-</span>
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
</template>

<style scoped></style>
