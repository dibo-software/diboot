<script setup lang="ts">
import type { Dictionary } from '@/views/system/dictionary/type'

const visible = ref(false)

const { loadData, loading, model } = useDetail<Dictionary>('/dictionary')

const open = async (id: string) => {
  loadData(id)
  visible.value = true
}
defineExpose({
  open
})
</script>
<template>
  <el-dialog v-model="visible" :width="650" title="详情">
    <el-descriptions v-if="model" v-loading="loading" class="margin-top" :column="2" border>
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
          <template v-if="scope.row.extension?.color">
            <span class="color-block" :style="{ background: scope.row.extension?.color }" />
          </template>
          <template v-else> - </template>
        </template>
      </el-table-column>
    </el-table>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="visible = false">关闭</el-button>
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
