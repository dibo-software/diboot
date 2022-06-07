<script setup lang="ts" name="DictionaryList">
import { ArrowDown, Refresh, Search } from '@element-plus/icons-vue'
import FormPage from './form.vue'
import DetailPage from './detail.vue'
interface Dictionary {
  id: string
  parentId: string
  type: string
  itemName: string
  itemValue?: string
  description: string
  children: Dictionary[]
  createTime: string
}

const { queryParam, dateRangeQuery, loading, dataList, pagination, getList, onSearch, resetFilter, remove } =
  useListDefault<Dictionary>({
    baseApi: '/dictionary'
  })

getList()

const searchState = ref(false)

const formPage = ref()
const detailPage = ref()
const openForm = (id?: string) => {
  formPage.value?.open(id)
}
const openDetail = (id: string) => {
  detailPage.value?.open(id)
}
</script>
<template>
  <div class="table-page">
    <el-form v-show="searchState" label-width="80px" class="list-search" @submit.prevent>
      <el-row :gutter="18">
        <el-col :md="8" :sm="24">
          <el-form-item label="菜单名称">
            <el-input v-model="queryParam.itemName" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="菜单编码">
            <el-input v-model="queryParam.itemValue" clearable @change="onSearch" />
          </el-form-item>
        </el-col>
        <el-col :md="8" :sm="24">
          <el-form-item label="创建时间">
            <date-range v-model="dateRangeQuery.createTime" @change="onSearch" />
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
    <el-space wrap class="list-operation">
      <el-button type="primary" @click="openForm()">新建</el-button>
      <el-space>
        <el-button :icon="Refresh" circle @click="getList()" />
        <el-button :icon="Search" circle @click="searchState = !searchState" />
      </el-space>
    </el-space>
    <el-table
      v-loading="loading"
      row-key="id"
      :tree-props="{ children: 'children__' }"
      :data="dataList"
      stripe
      height="100%"
    >
      <el-table-column type="expand">
        <template #default="props">
          <template v-if="props.row.children && props.row.children.length > 0">
            <div class="dict-item-wrapper">
              <el-tag
                v-for="item in props.row.children"
                :key="item.itemValue"
                type="info"
                effect="dark"
                class="dict-item"
                style="border: none"
                :color="item.color ? item.color : undefined"
              >
                {{ item.itemName }}({{ item.itemValue }})
              </el-tag>
            </div>
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="itemName" label="类型名称" />
      <el-table-column label="类型编码">
        <template #default="{ row }">
          <template v-if="row.parentId && row.parentId !== '0'"> {{ row.itemValue }} </template>
          <template v-else>
            {{ row.type }}
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="备注" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <template v-if="!row.parentId || row.parentId === '0'">
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
                    <el-dropdown-item @click="openForm(row.id)">编辑</el-dropdown-item>
                    <el-dropdown-item @click="remove(row.id)">删除</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </el-space>
          </template>
          <span v-else>-</span>
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

    <form-page ref="formPage" @complete="getList()" />
    <detail-page ref="detailPage" />
  </div>
</template>
<style lang="scss">
.dict-item-wrapper {
  box-sizing: border-box;
  padding: 0 50px;
}
.dict-item {
  margin-right: 8px;
}
</style>
