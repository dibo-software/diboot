<script setup lang="ts" name="Message">
import { Search, CircleClose, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import type { Message } from './type'
import Detail from '@/views/system/message/detail.vue'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter } = useListDefault<Message>({
  baseApi: '/message'
})
getList()

const advanced = ref(false)

const detailRef = ref()
const openDetail = (id: string) => {
  detailRef.value?.open(id)
}

const { more, initMore } = useMoreDefault({ dict: ['MESSAGE_CHANNEL', 'MESSAGE_STATUS'] })
initMore()
</script>

<template>
  <div class="list-page">
    <el-header>
      <el-form label-width="80px" class="list-search" @submit.prevent>
        <el-row :gutter="18">
          <el-col :md="6" :sm="12">
            <el-form-item label="发送通道">
              <el-select v-model="queryParam.channel" placeholder="请选择发送通道" clearable @change="onSearch">
                <el-option
                  v-for="item in more.messageChannelOptions || []"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :md="6" :sm="12">
            <el-form-item label="标题">
              <el-input v-model="queryParam.title" clearable @change="onSearch" />
            </el-form-item>
          </el-col>
          <el-col :md="6" :sm="12">
            <el-form-item label="消息状态">
              <el-select v-model="queryParam.status" clearable placeholder="请选择消息状态" @change="onSearch">
                <el-option
                  v-for="item in more.messageStatusOptions || []"
                  :key="item.value"
                  :value="item.value"
                  :label="item.label"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <template v-if="advanced">
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
          </template>
          <el-col :md="6" :sm="12" style="margin-left: auto">
            <el-form-item>
              <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
              <el-button :icon="CircleClose" title="重置搜索条件" @click="resetFilter" />
              <el-button
                :icon="advanced ? ArrowUp : ArrowDown"
                :title="advanced ? '收起' : '展开'"
                @click="advanced = !advanced"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-header>
    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" height="100%">
      <el-table-column prop="businessType" label="业务类型">
        <template #default="{ row }">
          <span>{{ row.businessType }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="sender" label="发送方">
        <template #default="{ row }">
          <span>{{ row.sender }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="receiver" label="接收方">
        <template #default="{ row }">
          <span>{{ row.receiver }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="channelLabel" label="发送通道">
        <template #default="{ row }">
          <el-tag type="info">{{ row.channelLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="statusLabel" label="发送状态">
        <template #default="{ row }">
          <el-tag v-if="row.status === 'FAILED'" type="danger">{{ row.statusLabel }}</el-tag>
          <el-tag v-else-if="row.status === 'DELIVERY' || row.status === 'READ'" type="success">
            {{ row.statusLabel }}
          </el-tag>
          <el-tag v-else type="info">{{ row.statusLabel }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="70">
        <template #default="{ row }">
          <el-button text bg type="primary" size="small" @click="openDetail(row.id)">详情</el-button>
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
  </div>
</template>

<style scoped></style>
