<script setup lang="ts" name="Dictionary">
import { ArrowDown, Search, Plus } from '@element-plus/icons-vue'
import FormPage from './Form.vue'
import DetailPage from './Detail.vue'
import type { Dictionary } from '@/views/system/dictionary/type'

interface DictionarySearch extends Dictionary {
  keywords?: string
}

type DictionaryTableExpand = Dictionary & {
  isExpand?: boolean | undefined
}

const { queryParam, loading, dataList, pagination, getList, onSearch, remove, resetFilter } = useList<
  DictionaryTableExpand,
  DictionarySearch
>({
  baseApi: '/dictionary'
})

getList()

const formPage = ref()
const detailPage = ref()
const table = ref()
const openForm = (id?: string) => {
  formPage.value?.open(id)
}
const openDetail = (id: string) => {
  detailPage.value?.open(id)
}

const updatePermission = checkPermission('update')
const deletePermission = checkPermission('delete')

function rowClick(row: DictionaryTableExpand) {
  const index: number = dataList.findIndex(v => v.id === row.id)
  const { isExpand } = dataList[index]
  dataList[index].isExpand = !isExpand
  table.value?.toggleRowExpansion(row, !isExpand)
}
</script>
<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" :icon="Plus" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input v-model="queryParam.itemName" clearable :placeholder="$t('dictionary.itemName')" @change="onSearch" />
        <el-input v-model="queryParam.type" clearable :placeholder="$t('dictionary.type')" @change="onSearch" />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table
      ref="table"
      v-loading="loading"
      class="list-body"
      row-key="id"
      :tree-props="{ children: 'children__' }"
      :data="dataList"
      stripe
      height="100%"
      @row-click="rowClick"
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
                :color="item.extension?.color"
              >
                {{ item.itemName }}({{ item.itemValue }})
              </el-tag>
            </div>
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="itemName" :label="$t('dictionary.itemName')" />
      <el-table-column :label="$t('dictionary.type')">
        <template #default="{ row }">
          <template v-if="row.parentId && row.parentId !== '0'"> {{ row.itemValue }} </template>
          <template v-else>
            {{ row.type }}
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="description" :label="$t('dictionary.description')" />
      <el-table-column prop="createTime" :label="$t('baseField.createTime')" width="185" />
      <el-table-column :label="$t('operation.label')" width="160" fixed="right">
        <template #default="{ row }">
          <template v-if="!row.parentId || row.parentId === '0'">
            <el-space>
              <el-button
                v-has-permission="'detail'"
                text
                bg
                type="primary"
                size="small"
                @click.stop="openDetail(row.id)"
              >
                {{ $t('operation.detail') }}
              </el-button>
              <el-dropdown v-has-permission="['update', 'delete']">
                <el-button text bg type="primary" size="small">
                  {{ $t('operation.more') }}
                  <el-icon :size="16" style="margin-left: 5px">
                    <arrow-down />
                  </el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-if="updatePermission" @click="openForm(row.id)">
                      {{ $t('operation.update') }}
                    </el-dropdown-item>
                    <el-dropdown-item v-if="deletePermission" @click="remove(row.id)">
                      {{ $t('operation.delete') }}
                    </el-dropdown-item>
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

    <FormPage ref="formPage" @complete="getList()" />
    <DetailPage ref="detailPage" />
  </div>
</template>
<style lang="scss">
.dict-item-wrapper {
  box-sizing: border-box;
  padding: 0 50px;
}

.dict-item {
  margin-right: 8px;
  margin-top: 4px;
  margin-bottom: 4px;
}
</style>
