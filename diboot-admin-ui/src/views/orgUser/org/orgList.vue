<script setup name="OrgList" lang="ts">
import type { OrgModel } from '@/views/orgUser/org/type'
import { Refresh } from '@element-plus/icons-vue'
import OrgForm from './form.vue'
import { BaseListPageLoader } from '@/hooks/list'

type Props = {
  parentId?: string
}
const props = withDefaults(defineProps<Props>(), {
  parentId: '0'
})

const emit = defineEmits(['reload'])

// 自定义列表页pageLoader
class OrgListPageLoader extends BaseListPageLoader<OrgModel> {
  public afterRemoveSuccess(ids: Array<string>) {
    // 发送数据重载事件
    emit('reload')
    super.afterRemoveSuccess(ids)
  }
}
const { pageLoader, customQueryParam, dataList, pagination } = useList<OrgModel>({
  options: {
    baseApi: '/org'
  }
})
watch(
  () => props.parentId,
  val => {
    if (customQueryParam) {
      ;(customQueryParam.value as any).parentId = val
    }
    pageLoader.onSearch()
  }
)

const formRef = ref()
const openForm = (id?: string) => {
  formRef.value?.open(id)
}

const onFormComplete = (id?: string) => {
  if (id) {
    pageLoader.getList()
  } else {
    pageLoader.onSearch()
  }
  emit('reload')
}

const onSearch = () => {
  pageLoader.onSearch()
}
defineExpose({ onSearch })
</script>
<template>
  <div style="width: 100%">
    <el-space wrap class="list-operation">
      <el-button type="primary" @click="openForm()">新建</el-button>
      <el-space>
        <el-button :icon="Refresh" circle @click="pageLoader.getList()" />
      </el-space>
    </el-space>
    <el-table row-key="id" :tree-props="{ children: 'children__' }" :data="dataList" stripe>
      <el-table-column prop="name" label="全称" />
      <el-table-column prop="shortName" label="简称" />
      <el-table-column prop="code" label="编码" />
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" width="140">
        <template #default="{ row }">
          <el-button text bg type="primary" size="small" @click="openForm(row.id)">编辑</el-button>
          <el-button text bg type="danger" size="small" @click="pageLoader.remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-if="pagination?.total"
      v-model:currentPage="pagination.current"
      v-model:page-size="pagination.pageSize"
      :page-sizes="[10, 20, 30, 50, 100]"
      background
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      @size-change="pageLoader.getList()"
      @current-change="pageLoader.getList()"
    />
    <org-form ref="formRef" :parent-id="props.parentId" @complete="onFormComplete" />
  </div>
</template>
