<script setup lang="ts" name="I18nConfig">
import { Search, CircleClose } from '@element-plus/icons-vue'
import type { I18nConfig } from './type'
import Form from './form.vue'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, batchRemove } = useList<
  I18nConfig[],
  I18nConfig
>({ baseApi: '/i18n-config' })
pagination.orderBy = { code: 'ASC' }
getList()

const formRef = ref()
const openForm = (code: string) => {
  formRef.value?.open(code)
}

// 用于选择
const props = defineProps<{ modelValue?: string; select?: boolean }>()
const emits = defineEmits<{
  (e: 'update:modelValue', code: string): void
  (e: 'change', list: Array<I18nConfig>): void
}>()
const single = ref(props.modelValue)
if (props.select) {
  watch(
    () => props.modelValue,
    value => (single.value = value)
  )
}
const singleRow = (row: Array<I18nConfig>) => {
  // single.value = row[0].code
  emits('update:modelValue', single.value as string)
  emits('change', row)
}
</script>

<template>
  <div class="list-page">
    <el-form label-width="80px" class="list-search" @submit.prevent>
      <el-row style="align-items: center; flex-direction: row-reverse; margin: 0 !important">
        <el-col :lg="18" :md="24">
          <el-row :gutter="8">
            <el-col :lg="8" :md="12">
              <el-form-item label="资源标识">
                <el-input v-model="queryParam.code" clearable @change="onSearch" />
              </el-form-item>
            </el-col>
            <el-col :lg="8" :md="12">
              <el-form-item label="翻译内容">
                <el-input v-model="queryParam.content" clearable @change="onSearch" />
              </el-form-item>
            </el-col>
            <el-col :lg="8" :md="12" style="margin-left: auto">
              <el-form-item>
                <el-button :icon="Search" type="primary" @click="onSearch">搜索</el-button>
                <el-button :icon="CircleClose" title="重置搜索条件" @click="resetFilter" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-col>
        <el-col :lg="6" :md="24">
          <el-button v-has-permission="'create'" type="primary" style="margin-left: 10px" @click="openForm()">
            {{ $t('operation.create') }}
          </el-button>
        </el-col>
      </el-row>
    </el-form>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" height="100%">
      <el-table-column v-if="select" fixed width="30">
        <template #default="{ row }">
          <el-radio v-model="single" :label="row[0].code" @change="singleRow(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="code" label="资源标识" fixed show-overflow-tooltip min-width="180px">
        <template #default="{ row }">
          <span>{{ row[0].code }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="!select" prop="typeLabel" label="类型" fixed show-overflow-tooltip width="80px">
        <template #default="{ row }">
          <span>{{ row[0].typeLabel }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-for="locale in $i18n.availableLocales.map(e => e.replaceAll('-', '_'))"
        :key="locale"
        :prop="locale"
        :label="$t('language', null, { locale: locale.replaceAll('_', '-') })"
        show-overflow-tooltip
        min-width="180px"
      >
        <template #default="{ row }">
          <span>{{ row.find(e => e.language === locale)?.content }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!select"
        v-has-permission="['update', 'delete']"
        label="操作"
        width="160px"
        align="center"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row[0].code)">
            {{ $t('operation.update') }}
          </el-button>
          <el-button
            v-has-permission="'delete'"
            text
            bg
            type="danger"
            size="small"
            @click="batchRemove(row.map(e => e.id))"
          >
            {{ $t('operation.delete') }}
          </el-button>
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

    <Form ref="formRef" @complete="getList()" />
  </div>
</template>
