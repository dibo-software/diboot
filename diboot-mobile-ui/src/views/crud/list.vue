<script setup lang="ts">

const { dataList, loading, pagination, queryParam, onSearch, nextPage, resetFilter } = useList({
  baseApi: '/iam/user'
})

const refreshing = ref(false)
const finished = computed(() => dataList.length === pagination.total)

const router = useRouter()

// 打开详情页
const openDetail = (id: string) => {
  router.push({
    name: 'CrudDetail',
    params: { id },
  })
}
// 打开表单页面
const openForm = (id: string) => {
  router.push({
    name: 'CrudForm',
    params: { id },
  })
}
</script>

<template>
  <van-search
      v-model="queryParam.name"
      show-action
      shape="round"
      placeholder="请输入搜索关键词"
      @search="onSearch"
      @blur="onSearch"
      @clear="resetFilter"
      @cancel="resetFilter"
  />
  <van-pull-refresh v-model="refreshing" style="height: calc(100% - 55px); overflow-y: auto" @refresh="onSearch">
    <van-list v-model:loading="loading" :finished="finished" finished-text="没有更多了" @load="nextPage">
      <van-swipe-cell v-for="item in dataList" :key="item.id">
        <van-cell
            :border="false"
            :title="`D:${item.id}`"
            @click="() => openDetail(item.id)"
        >
          <template #label>
            <div>姓名：{{ item.realname }}</div>
            <div>创建时间：{{ item.createTime }}</div>
          </template>
        </van-cell>
        <template #right>
          <van-button square type="primary" @click="() => openForm(item.id)" text="编辑" />
        </template>
      </van-swipe-cell>
    </van-list>
  </van-pull-refresh>
</template>

<style scoped lang="scss">
:deep(.van-cell__title),
:deep(.van-cell__value) {
  flex: auto;
}

.fh {
  height: 100%;
}
:deep(.van-swipe-cell__right) {
  .van-button {
    height: 100%;
  }
}
</style>
