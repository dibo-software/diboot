<script setup lang="ts">
import { ChatDotRound, EditPen, Delete } from '@element-plus/icons-vue'
import useChatAiStore from '@/store/chat-ai'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const chatAiStore = useChatAiStore()
const { currentSession, sessions } = storeToRefs(chatAiStore)

// 加载所有session
chatAiStore.getSessions()

const editSession = ref()
const isOutSide = (e: any, className: string): boolean => {
  const element: Element | null = document.querySelector(`.${className}`)
  return element ? !element.contains(e.target) : true
}
/**
 * 更新session
 */
const updateSession = (e: any) => {
  if (editSession.value) {
    if (!isOutSide(e, `session-item_${editSession.value.id}`)) return
    // 更新编辑器
    sessions.value.forEach(item => (item.edit = false))
    chatAiStore.updateSession(editSession.value).then(() => chatAiStore.getSessions())
    // 恢复session
    editSession.value = undefined
    document.removeEventListener('click', updateSession)
  }
}
const loading = ref(false)

/**
 * 创建对话
 */
const handleCreate = async () => {
  loading.value = true
  await chatAiStore.createSession(i18n.t('chatAi.newSession') + new Date().getTime())
  loading.value = false
  chatAiStore.getSessions()
}
/**
 * 编辑session
 * @param session
 */
const handleEdit = (session: any) => {
  document.addEventListener('click', updateSession)
  sessions.value.forEach(item => (item.edit = false))
  session.edit = true
  editSession.value = session
}
/**
 * 删除session
 */
const handleDel = async (session: any) => {
  await chatAiStore.removeSession(session.id)
  chatAiStore.getSessions()
}
</script>

<template>
  <el-aside width="200px" class="chat-ai-aside">
    <div class="new-session">
      <el-button
        :loading="loading"
        type="primary"
        style="width: 100%"
        size="large"
        :icon="ChatDotRound"
        @click="handleCreate"
        >{{ $t('chatAi.createSession') }}</el-button
      >
    </div>
    <el-scrollbar>
      <div
        v-for="(session, index) in sessions"
        :key="`session_${index}`"
        :class="`session-item ${session.id === currentSession?.id ? 'session-active' : ''} session-item_${session.id}`"
        @click="() => chatAiStore.chooseSession(session)"
      >
        <div v-if="!session.edit" class="title">{{ session.title }}</div>
        <div v-else>
          <el-input v-model="session.title" />
        </div>
        <div v-if="!session.edit" class="session-operate">
          <el-icon
            color="#E6A23C"
            :title="$t('chatAi.updateSession')"
            style="margin-right: 5px"
            @click.stop="handleEdit(session)"
          >
            <EditPen />
          </el-icon>
          <el-icon color="#F56C6C" :title="$t('chatAi.deleteSession')" @click.stop="handleDel(session)">
            <Delete />
          </el-icon>
        </div>
      </div>
    </el-scrollbar>
  </el-aside>
</template>

<style scoped lang="scss">
.chat-ai-aside {
  border-right: var(--el-border-color) solid 1px;
  .new-session {
    padding: 10px;
    box-sizing: border-box;
  }

  :deep(.el-scrollbar) {
    height: calc(100% - 60px);
  }

  .session-operate {
    display: none;
    width: 80px;
    position: absolute;
    right: 0;
    top: 50%;
    transform: translateY(-21px);
    text-align: right;
    padding-right: 20px;

    :deep(.el-icon) {
      cursor: pointer;
    }
  }

  .session-item {
    position: relative;
    padding: 0 5px;
    line-height: 42px;
    box-sizing: border-box;
    cursor: pointer;
    .title {
      font-size: var(--el-font-size-dynamic);
      padding-left: 5px;
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
    }
    &:hover {
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);

      .session-operate {
        display: inline-block;
        background: linear-gradient(to right, transparent, #fff, #fff);
      }
    }
  }

  .session-active {
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }
}
</style>
