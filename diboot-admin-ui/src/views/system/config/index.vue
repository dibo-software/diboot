<script setup lang="ts" name="SystemConfig">
import Category from './Category.vue'
import List from './List.vue'

const baseApi = '/system-config'
const type = ref<string>()
const categoryList = ref<string[]>([])

const refresh = () => {
  api.get<string[]>(`${baseApi}/category`).then(res => {
    categoryList.value = res.data ?? []
    if (!type.value) type.value = categoryList.value?.length > 0 ? categoryList.value[0] : '_ext_'
  })
}

refresh()
</script>

<template>
  <div style="padding: 10px">
    <el-tabs v-model="type">
      <template v-for="item in categoryList" :key="item">
        <el-tab-pane v-if="item" :name="item" :label="item">
          <Category v-if="type === item" :category="item" />
        </el-tab-pane>
      </template>

      <el-tab-pane name="_ext_" :label="$t('config.ext')">
        <List :category-list="categoryList" @refresh="refresh" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style scoped lang="scss"></style>
