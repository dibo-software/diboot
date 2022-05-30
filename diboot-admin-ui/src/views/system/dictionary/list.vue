<script setup lang="ts" name="DictionaryList">
import { ArrowDown, ArrowUp } from '@element-plus/icons-vue'
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

defineProps<{ usedVisibleHeight?: number }>()

const { pageLoader, advanced, queryParam, dateRangeQuery, dataList, pagination } = useList<Dictionary>({
  options: {
    baseApi: '/dictionary'
  }
})
// 选中的数据 Id 集合
const multipleSelectionIds = ref<string[]>([])
const formPage = ref(null)
const detailPage = ref(null)
const create = () => {
  if (formPage?.value) {
    ;(formPage as any).value.open()
  }
}
const openEdit = (id: string) => {
  ;(formPage as any).value.open(id)
}
const openDetail = (id: string) => {
  ;(detailPage as any).value.open(id)
}
</script>
<template>
  <el-form label-width="80px" @submit.prevent>
    <el-row :gutter="18">
      <el-col :md="8" :sm="24">
        <el-form-item label="菜单名称">
          <el-input v-model="queryParam.itemName" @keyup.enter="pageLoader.onDebounceSearch()" />
        </el-form-item>
      </el-col>
      <el-col :md="8" :sm="24">
        <el-form-item label="菜单编码">
          <el-input v-model="queryParam.itemValue" @keyup.enter="pageLoader.onDebounceSearch()" />
        </el-form-item>
      </el-col>
      <template v-if="advanced">
        <el-col :md="8" :sm="24">
          <el-form-item label="创建时间">
            <el-date-picker
              v-model="dateRangeQuery.createTime"
              type="daterange"
              value-format="YYYY-MM-DD"
              @change="pageLoader.onDebounceSearch"
            />
          </el-form-item>
        </el-col>
      </template>
      <el-col :md="8" :sm="24" style="margin-left: auto">
        <el-form-item>
          <el-button type="primary" @click="pageLoader.onDebounceSearch">搜索</el-button>
          <el-button @click="pageLoader.onReset()">重置</el-button>
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
    <el-button type="primary" @click="create">新建</el-button>
  </el-space>
  <el-table
    :max-height="`calc(100vh - 120px - ${usedVisibleHeight}px)`"
    :data="dataList"
    row-key="id"
    :tree-props="{ children: 'children__' }"
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
                  <el-dropdown-item @click="openEdit(row.id)">编辑</el-dropdown-item>
                  <el-dropdown-item @click="pageLoader.remove(row.id)">删除</el-dropdown-item>
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
    v-show="pagination.total > 0"
    v-model:currentPage="pagination.current"
    v-model:page-size="pagination.pageSize"
    :page-sizes="[10, 20, 30, 50, 100]"
    background
    layout="total, sizes, prev, pager, next, jumper"
    :total="pagination.total"
    @size-change="pageLoader.getList()"
    @current-change="pageLoader.getList()"
  />
  <form-page ref="formPage" @complete="pageLoader.onSearch()" />
  <detail-page ref="detailPage" />
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
