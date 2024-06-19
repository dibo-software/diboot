<script setup lang="ts" name="I18nConfig">
import { Search, Plus } from '@element-plus/icons-vue'
import type { I18nConfig } from './type'
import Form from './Form.vue'
import { useI18n } from 'vue-i18n'

const { queryParam, loading, dataList, pagination, getList, onSearch, resetFilter, batchRemove } = useList<
  I18nConfig[],
  I18nConfig
>({ baseApi: '/i18n-config' })
pagination.orderBy = 'code:ASC'
getList()

const formRef = ref()
const openForm = (code?: string) => {
  formRef.value?.open(code)
}

// 用于选择
const props = defineProps<{ modelValue?: string; select?: boolean; tableHeight?: string }>()
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

// 国际化
const i18n = useI18n()
const locales = i18n.availableLocales.map(e => e.replace(/-/g, '_'))

const initI18nData = () => {
  const initData: Partial<I18nConfig>[] = Array.of(...locales).map(locale => ({ language: locale }))
  const zh = initData.find(item => item.language === 'zh_CN')
  const en = initData.find(item => item.language === 'en')
  const rest = initData.filter(item => item.language !== 'zh_CN' && item.language !== 'en')
  const arr: Partial<I18nConfig>[] = []
  if (zh) arr.push(zh)
  if (en) arr.push(en)
  if (rest && rest.length > 0) rest.forEach(item => arr.push(item))
  return arr
}
</script>

<template>
  <div class="list-page">
    <el-space wrap class="list-operation">
      <el-button v-has-permission="'create'" :icon="Plus" type="primary" @click="openForm()">
        {{ $t('operation.create') }}
      </el-button>
      <el-space>
        <el-input v-model="queryParam.code" :placeholder="$t('i18nConfig.i18nCode')" clearable @change="onSearch" />
        <el-input v-model="queryParam.content" :placeholder="$t('i18nConfig.content')" clearable @change="onSearch" />
        <el-button :icon="Search" type="primary" @click="onSearch">{{ $t('operation.search') }}</el-button>
        <el-button :title="$t('title.reset')" @click="resetFilter">{{ $t('operation.reset') }}</el-button>
      </el-space>
    </el-space>

    <el-table ref="tableRef" v-loading="loading" class="list-body" :data="dataList" :height="tableHeight || '100%'">
      <el-table-column v-if="select" fixed width="36px">
        <template #default="{ row }">
          <el-radio v-model="single" :label="row[0].code" @change="singleRow(row)">{{ '' }}</el-radio>
        </template>
      </el-table-column>
      <el-table-column prop="code" :label="$t('i18nConfig.i18nCode')" fixed show-overflow-tooltip min-width="180px">
        <template #default="{ row }">
          <span>{{ row[0].code }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!select"
        prop="typeLabel"
        :label="$t('i18nConfig.type')"
        fixed
        show-overflow-tooltip
        width="90px"
      >
        <template #default="{ row }">
          <el-tag :color="row[0].typeLabel?.ext?.color" effect="dark" type="info">
            {{ row[0].typeLabel?.label }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column
        v-for="item in initI18nData()"
        :key="item?.language"
        :prop="item?.language"
        :label="$t('language', {}, { locale: item?.language?.replace(/_/g, '-') })"
        show-overflow-tooltip
        min-width="180px"
      >
        <template #default="{ row }">
          <span>{{ row.find((e: I18nConfig) => e.language === item?.language)?.content }}</span>
        </template>
      </el-table-column>
      <el-table-column
        v-if="!select"
        v-has-permission="['update', 'delete']"
        :label="$t('operation.label')"
        width="160px"
        header-align="center"
        fixed="right"
      >
        <template #default="{ row }">
          <el-button v-has-permission="'update'" text bg type="primary" size="small" @click="openForm(row[0].code)">
            {{ $t('operation.update') }}
          </el-button>
          <el-button
            v-if="!row.some((e: I18nConfig) => e.type === 'SYSTEM')"
            v-has-permission="'delete'"
            text
            bg
            type="danger"
            size="small"
            @click="batchRemove(row.map((e: I18nConfig) => e.id))"
          >
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
