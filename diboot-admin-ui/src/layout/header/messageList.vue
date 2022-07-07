<script setup lang="ts">
import { Bell } from '@element-plus/icons-vue'
import useMessageStore from '@/store/message'
import type { MessageInfo } from '@/store/message'
import moment from 'moment'

const messageStore = useMessageStore()

// 自动获取新消息
messageStore.loadNewMessages()
setTimeout(() => messageStore.loadNewMessages(), 5 * 60 * 1_000)

// 抽屉关闭时改变消息状态
watch(
  () => messageStore.show,
  value => {
    if (value) return
    messageStore.list.forEach(e => (e.new = false))
  }
)

const dialogVisible = ref(false)
const messageInfo = ref<MessageInfo>()

const handleClick = (message: MessageInfo) => {
  messageStore.remove(message.id)
  messageInfo.value = message
  dialogVisible.value = true
}
</script>

<template>
  <div>
    <el-badge :value="messageStore.list.length" :hidden="!messageStore.list.length" @click="messageStore.show = true">
      <el-icon :size="22">
        <Bell />
      </el-icon>
    </el-badge>

    <el-drawer v-model="messageStore.show" title="新消息">
      <el-empty v-if="messageStore.list.length === 0" description="暂无消息" />
      <el-scrollbar v-else>
        <TransitionGroup name="fade">
          <el-card v-for="item in messageStore.list" :key="item.id" shadow="hover" @click="handleClick(item)">
            <template #header>
              <div class="header">
                <span class="title">{{ item.title }} <el-badge v-show="item.new" is-dot /></span>
                <span class="source">来源：{{ item.source }}</span>
              </div>
            </template>
            <div class="content">
              <div class="value" v-html="item.content" />
              <div class="end">
                <span>{{ moment(item.time, 'YYYY-MM-DD HH:mm:ss').fromNow() }}</span>
              </div>
            </div>
          </el-card>
        </TransitionGroup>
      </el-scrollbar>
    </el-drawer>

    <el-dialog v-model="dialogVisible" :title="messageInfo?.title">
      <el-scrollbar max-height="55vh">
        <div v-html="messageInfo?.content" />
      </el-scrollbar>
    </el-dialog>
  </div>
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
