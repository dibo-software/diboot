<script setup lang='ts'>

interface CustomInfo {
  title: string,
  name: string,
  contacts: string,
  telephone: string,
  department: string,
  date: string,
}

const list = ref<CustomInfo[]>([])
const loading = ref(false)
const finished = ref(false)
const error = ref(false)
const refreshing = ref(false)

const onLoad = () => {
  setTimeout(() => {
    if (refreshing.value) {
      list.value = []
      refreshing.value = false
    }

    for (let i = 0; i < 10; i++) {
      list.value.push({
        title: `来访客户${list.value.length + 1}`,
        name: '帝博软件',
        contacts: '张三',
        telephone: '18889889878',
        department: '研发部',
        date: '2023-05-20'
      })
      if (list.value.length === 20) error.value = true
    }
    loading.value = false

    if (list.value.length >= 40) {
      finished.value = true
    }
  }, 1000)
}

const onRefresh = () => {
  // 清空列表数据
  finished.value = false

  // 重新加载数据
  // 将 loading 设置为 true，表示处于加载状态
  loading.value = true
  onLoad()
}

const handleRemove = (title: string) => {
  showConfirmDialog({ title: '您确定删除这条数据吗？', message: '删除后无法找回！' }).then(() => {
    loading.value = true
    list.value = list.value.filter(data => data.title !== title)
    loading.value = false
  })
}

const searchName = ref('')
const onSearch = () => {
  list.value = list.value.filter(data => data.title.indexOf(searchName.value) > -1)
}
const resetFilter = () => {
  list.value = []
  error.value = false
  onRefresh()
}
</script>

<template>
  <van-search
    v-model='searchName'
    show-action
    shape='round'
    placeholder='请输入搜索关键词'
    @search='onSearch'
    @blur='onSearch'
    @clear='resetFilter'
    @cancel='resetFilter'
  />
  <van-pull-refresh class='bgcolor' v-model='refreshing' @refresh='onRefresh'
                    :style="{ height: 'calc(100vh - 150px)', overflowY: 'auto' }">
    <van-list
      v-model:loading='loading'
      :finished='finished'
      finished-text='没有更多了'
      v-model:error='error'
      error-text='请求失败，点击重新加载'
      @load='onLoad'
    >
      <van-swipe-cell v-for='item in list' :key='item'>
        <van-cell-group inset style='margin-top: 10px'>
          <van-cell :border='false' :title='item.title' @click="$router.push({ name: 'DetailExample' })">
            <template #value>
              <van-tag plain type='success'>查看详情</van-tag>
            </template>
            <template #label>
              <div>来访客户：{{ `${item.name}-${item.contacts}` }}</div>
              <div>联系电话：{{ item.telephone }}</div>
              <div>拜访部门：{{ item.department }}</div>
              <div>拜访时间：{{ item.date }}</div>
            </template>
          </van-cell>
        </van-cell-group>
        <template #right>
          <van-button square plain type="primary" text='编辑' class='fh' @click="$router.push({ name: 'FormExample' })" />
          <van-button square type='danger' text='删除' class='fh' @click='handleRemove(item.title)' />
        </template>
      </van-swipe-cell>
    </van-list>
  </van-pull-refresh>
  <van-button icon='plus' type='primary' class='footer' round block @click="$router.push({ name: 'FormExample' })">新增
  </van-button>


</template>

<style scoped lang='scss'>
.footer {
  position: fixed;
  bottom: 5px;
  width: 100%;
}

.fh {
  height: 100%;
}

.bgcolor {
  background: var(--van-gray-3)
}
</style>
