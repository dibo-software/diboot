<script lang="ts" setup>
import type { Message } from '@/views/system/message/type'
import { Loading } from '@element-plus/icons-vue'
import auth from '@/utils/auth'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const baseApi = '/message'

const props = defineProps<{ unread?: boolean }>()
const emit = defineEmits<{
  (e: 'total', total: number): void
  (e: 'reset'): void
  (e: 'close'): void
}>()

const dataList = ref<Message[]>([])
const dataIds = ref<string[]>([])
const total = ref(0)
const loading = ref(false)
const disabled = computed(() => loading.value || total.value === dataList.value.length)
const queryParam = ref({
  unread: props.unread,
  pageSize: 10,
  pageIndex: 1,
  total: 0
})

watch(total, value => emit('total', value))
const unreadIds = computed(() => dataList.value.filter(e => e.status !== 'READ').map(e => e.id))

const loadData = (reset = false) => {
  if (!auth.getToken()) return
  loading.value = true
  api
    .get<Message[]>(`${baseApi}/own`, queryParam.value)
    .then(res => {
      if (reset) {
        dataList.value = res.data || []
        dataIds.value = dataList.value.map(e => e.id)
      } else {
        const list = (res.data || []).filter(e => !dataIds.value.includes(e.id))
        dataList.value.push(...list)
        dataIds.value.push(...list.map(e => e.id))
      }
      queryParam.value.pageIndex = Number(res.page?.pageIndex || 1)
      total.value = res.page?.totalCount ? Number(res.page.totalCount) : 0
    })
    .catch(err => {
      ElMessage.error(i18n.t('layout.messageBell.fetchListError') + (err.msg || err.message || err))
    })
    .finally(() => (loading.value = false))
}

loadData()

// Message 自动刷新的时间间隔（分钟）
const MESSAGE_REFRESH_EXPIRE = 5
// 自动刷新计时器
let refreshTimer: NodeJS.Timeout
resetRefreshTimer()
/**
 * 重置自动刷新定时器
 */
function resetRefreshTimer() {
  clearTimeout(refreshTimer)
  refreshTimer = setTimeout(
    () => {
      refresh()
    },
    MESSAGE_REFRESH_EXPIRE * 60 * 1000
  )
}

const refresh = () => {
  queryParam.value.pageIndex = 1
  loadData(true)
  resetRefreshTimer()
}

defineExpose({ refresh })

const nextPage = () => {
  if (total.value === dataList.value.length) return
  ++queryParam.value.pageIndex
  loadData()
}

const markRead = async (ids: string[]) => {
  if (ids.length === 1) {
    const find = dataList.value.find(e => e.id === ids[0])
    if (!find) return
    if (find.status === 'READ') return
    if (!props.unread) Object.assign(find, { status: 'READ' })
  }
  api
    .post(`${baseApi}/read`, ids)
    .then(() => {
      if (ids.length > 1) refresh()
      total.value = Math.max(total.value - ids.length, 0)
      if (props.unread) {
        dataIds.value = dataIds.value.filter(id => !ids.includes(id))
        dataList.value = dataList.value.filter(e => !ids.includes(e.id))
      }
      emit('reset')
    })
    .catch(err => {
      console.error('消息标记已读失败', err)
    })
}

const handleClick = (data: Message) => {
  markRead([data.id])
  // 分类处理
  switch (data.businessType) {
    default: // 关闭 消息抽屉 打开对话框
      emit('close')
      messageInfo.value = data
      dialogVisible.value = true
  }
}

// 对话框
const dialogVisible = ref(false)
const messageInfo = ref<Message>()
</script>

<template>
  <el-scrollbar height="60vh">
    <div v-infinite-scroll="nextPage" name="fade" :infinite-scroll-disabled="disabled">
      <TransitionGroup>
        <el-card v-for="item in dataList" :key="item.id" shadow="hover" @click="handleClick(item)">
          <template #header>
            <div class="header">
              <span class="title">{{ item.title }} <el-badge v-show="item.status !== 'READ'" is-dot /></span>
              <span class="source">{{ $t('layout.messageBell.from') }}：{{ item.senderName }}</span>
            </div>
          </template>
          <div class="content">
            <div class="value" v-html="item.content" />
            <div class="end">
              <span>{{ item.createTime }}</span>
            </div>
          </div>
        </el-card>
      </TransitionGroup>
      <el-empty v-if="!dataList.length" :description="$t('layout.messageBell.noMessagesYet')" />
      <div v-if="disabled" class="bottom-blank">
        <el-icon v-if="loading" size="24">
          <Loading />
        </el-icon>
      </div>
    </div>
  </el-scrollbar>

  <el-button type="primary" ghost :disabled="unreadIds.length === 0" style="width: 100%" @click="markRead(unreadIds)">
    {{ $t('layout.messageBell.allMarkRead') }}
  </el-button>

  <el-dialog v-model="dialogVisible" :title="messageInfo?.title" append-to-body>
    <el-scrollbar max-height="55vh">
      <div v-html="messageInfo?.content" />
    </el-scrollbar>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-card {
  margin-bottom: 10px;
  --el-card-padding: calc(var(--el-font-size-dynamic) - 1px);
}

.header {
  display: flex;
  justify-content: space-between;

  .title {
    width: 220px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    font-weight: bold;
  }

  .source {
    color: var(--el-text-color-secondary);
    font-size: var(--el-font-size-dynamic);
  }
}

.content {
  .value {
    font-size: var(--el-font-size-dynamic);
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box; /*重点，不能用block等其他*/
    -webkit-line-clamp: 2; /*重点IE和火狐不支持*/
    -webkit-box-orient: vertical; /*重点*/
  }

  .end {
    display: flex;
    justify-content: flex-end;
    margin-top: 5px;
    color: var(--el-text-color-secondary);
    font-size: calc(var(--el-font-size-dynamic) - 1px);
  }
}

.bottom-blank {
  height: 30px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 1. declare transition */
.fade-move,
.fade-enter-active,
.fade-leave-active {
  transition: all 0.5s cubic-bezier(0.55, 0, 0.1, 1);
}

/* 2. declare enter from and leave to state */
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: scaleY(0.01) translate(30px, 0);
}

/* 3. ensure leaving items are taken out of layout flow so that moving
      animations can be calculated correctly. */
.fade-leave-active {
  position: absolute;
}
</style>
