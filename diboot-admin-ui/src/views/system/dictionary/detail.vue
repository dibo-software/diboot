<script setup lang="ts" name="DictionaryDetail">
import useForm from '@/hooks/detail'
import type { Dictionary } from '@/views/system/dictionary/type'

type Props = {
  type?: string
  width?: number | string
}
withDefaults(defineProps<Props>(), {
  type: 'modal',
  width: 720
})

const { pageLoader, title, model, visible, loading } = useForm<Dictionary>({
  options: {
    baseApi: '/dictionary',
    title: '数据字典详情',
    model: {
      id: '',
      type: '',
      itemName: '',
      children: []
    }
  }
})

const open = async (id: string) => {
  pageLoader.open(id)
}
defineExpose({
  open
})
</script>
<template>
  <el-dialog v-model="visible" :width="width" :title="title">
    <el-descriptions v-if="model" class="margin-top" :column="2" border>
      <el-descriptions-item label="字典名称"> {{ model.itemName }} </el-descriptions-item>
      <el-descriptions-item label="字典编码"> {{ model.type }} </el-descriptions-item>
      <el-descriptions-item :span="2" label="字典备注"> {{ model.description }} </el-descriptions-item>
    </el-descriptions>
    <h3>字典条目</h3>
    <el-table v-if="model?.children" :data="model.children" style="width: 100%">
      <el-table-column width="250">
        <template #header> 条目名称 </template>
        <template #default="scope"> {{ scope.row.itemName }} </template>
      </el-table-column>
      <el-table-column width="250">
        <template #header> 条目编码 </template>
        <template #default="scope"> {{ scope.row.itemValue }} </template>
      </el-table-column>
      <el-table-column label="条目颜色" width="100">
        <template #default="scope">
          <template v-if="scope.row.color">
            <span class="color-block" :style="{ background: scope.row.color }" />
          </template>
          <template v-else> - </template>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="pageLoader.close()">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>
<style lang="scss">
.color-block {
  display: inline-block;
  height: 30px;
  width: 50px;
}
</style>
