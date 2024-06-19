<script setup lang="ts">
import { Position } from '@element-plus/icons-vue'
import ChatItem from './ChatItem.vue'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'

import useChatAiStore from '@/store/chat-ai'
import auth from '@/utils/auth'
import { api } from '@/utils/request'
type RoleType = 'system' | 'user' | 'assistant'
const chatAiStore = useChatAiStore()
const { currentMessages, currentSession } = storeToRefs(chatAiStore)

const inputMessage = ref('')
// 模型
const currentModel = ref<string>('qwen-turbo')
const modelOptions: LabelValue[] = [
  { label: '通义千问：qwen-turbo', value: 'qwen-turbo' },
  { label: '通义千问：qwen-plus', value: 'qwen-plus' },
  { label: '通义千问：qwen-max', value: 'qwen-max' },
  { label: '文心一言：Yi-34B-Chat', value: 'Yi-34B-Chat' },
  { label: 'Kimi：moonshot-v1-8k', value: 'moonshot-v1-8k' },
  { label: 'Kimi：moonshot-v1-32k', value: 'moonshot-v1-32k' },
  { label: 'Kimi：moonshot-v1-128k', value: 'moonshot-v1-128k' }
]
// 切换模型
const changeModel = (val: string) => (currentModel.value = val)

// 例子
const exampleList: { title: string; content: string }[] = [
  {
    title: '数学计算',
    content: '计算1+1'
  },
  {
    title: '代码生成',
    content: '请生成一个python代码，计算1+1'
  },
  {
    title: '写一封信',
    content: '写一封感谢信'
  },
  {
    title: '写一首诗',
    content: '写一首描述夏天的诗'
  }
]

/**
 * 动态计算正在使用的模型
 */
const useModel = computed(() => {
  const model = modelOptions.filter(item => item.value === currentModel.value)
  return model ? model[0].label : '通义千问：qwen-turbo'
})
//创建新消息
const createNewMessage = (content: string, role?: RoleType) => {
  return { role, content }
}
const chatScrollbarRef = ref()
const chatContentRef = ref()
const height = ref(0)

const handleScroll = async () => {
  await nextTick()
  height.value = chatContentRef.value?.clientHeight + 30 || 0
  chatScrollbarRef.value!.setScrollTop(height.value)
}
onMounted(() => handleScroll())
// 是否是新建的session
const newSession = ref(false)
watch(
  () => currentSession.value,
  async newVal => {
    if (newVal && !newSession.value) {
      await chatAiStore.loadSessionRecords(newVal.id as string)
      handleScroll()
    } else {
      newSession.value = false
    }
  },
  {
    immediate: true
  }
)
// 发送消息
const sendMessage = async (message: string, model: string) => {
  inputMessage.value = undefined
  if (!currentSession.value) newSession.value = true
  await chatAiStore.beforeSendMessage(message)
  const newMessage = createNewMessage(message, 'user')
  currentMessages.value.push(newMessage)
  //  截取最近3个发送到后端
  const cloneMessages = _.cloneDeep(currentMessages.value)
  // 添加一个空系统消息占位
  const systemMessage = createNewMessage('')

  currentMessages.value.push(systemMessage)

  const controller = new AbortController()
  const signal = controller.signal
  fetchEventSource('/api/ai-session-record/chat', {
    signal, // 传递信号以便可以中断请求
    openWhenHidden: true,
    method: 'POST',
    headers: {
      Authorization: auth.getToken() as string,
      'Content-Type': 'application/json;charset=utf-8'
    },
    body: JSON.stringify({
      // 截取最近3个发送到后端
      messages: cloneMessages.length >= 3 ? cloneMessages.slice(-3) : cloneMessages,
      model
    }),
    onmessage(ev) {
      // pattern 模式 REPLACE表示替换，另一种表示追加
      const { pattern, choices } = JSON.parse(ev.data)
      const choice = choices[0]
      const answerMessage = currentMessages.value[currentMessages.value.length - 1]
      answerMessage.role = choice.message.role
      if (pattern !== 'REPLACE') {
        if (!answerMessage.originContent) answerMessage.originContent = ''
        answerMessage.originContent += choice.message.content || ''
      }
      answerMessage.content = marked.parse(
        pattern === 'REPLACE' ? choice.message.content : answerMessage.originContent,
        {
          highlight: function (code, lang) {
            const language = hljs.getLanguage(lang) ? lang : 'plaintext'
            return hljs.highlight(code, { language }).value
          }
        }
      )
      if (choice.finish_reason === 'stop') {
        // 含有代码，最后通义刷新高亮
        const blocks = document.querySelectorAll('pre code')
        blocks.forEach(block => {
          hljs.highlightBlock(block)
        })
        delete answerMessage.originContent
        // 消息接收成功，消息存储至数据库
        api.post<boolean>(`/ai-session-record`, {
          sessionId: currentSession.value.id,
          model,
          requestBody: JSON.stringify(newMessage),
          responseBody: JSON.stringify(answerMessage)
        })
        // 如果响应结束，关闭请求
        controller.abort()
      }
      // 自动滚动
      handleScroll()
    }
  })
}
</script>

<template>
  <el-main class="chat-ai-main">
    <el-scrollbar ref="chatScrollbarRef">
      <div v-if="currentMessages.length > 0" ref="chatContentRef">
        <chat-item
          v-for="(message, index) in currentMessages"
          :key="`message_${index}`"
          :role="message.role"
          :message="message.content"
        />
      </div>
      <div v-else>
        <div class="chat-example">
          <p>{{ $t('chatAi.askMe') }}：</p>
          <el-row :gutter="24">
            <el-col v-for="item in exampleList" :key="item.title" :span="12" class="chat-example-item">
              <el-card shadow="hover">
                <div>{{ item.title }}</div>
                <div class="chat-example-item-content">{{ item.content }}</div>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </div>
    </el-scrollbar>
    <div class="chat-model">
      <span>{{ $t('chatAi.model') }}：</span>
      <el-select v-model="useModel" :placeholder="`${$t('placeholder.select')}`" @change="changeModel">
        <el-option v-for="item in modelOptions" :key="item.value" :label="item.label" :value="item.value" />
      </el-select>
    </div>
    <div class="chat-input">
      <el-input
        ref="inputRef"
        v-model="inputMessage"
        :rows="4"
        type="textarea"
        :placeholder="$t('chatAi.placeholder.problem')"
        resize="none"
      />
      <div class="chat-tools">
        <el-button
          type="primary"
          size="large"
          :title="$t('chatAi.title.send')"
          :icon="Position"
          @click="sendMessage(inputMessage, currentModel)"
        />
      </div>
    </div>
  </el-main>
</template>
<style scoped lang="scss">
.chat-ai-main {
  height: 100%;
  padding: 0;
  :deep(.el-scrollbar) {
    height: calc(100% - 100px - 62px);
  }
  .el-scrollbar {
    background-color: var(--el-menu-bg-color);
    border-radius: 10px;
  }
  .chat-model {
    display: flex;
    align-items: center;
    font-size: 14px;
    margin-bottom: 10px;
    margin-top: 20px;
    .el-select {
      width: 220px !important;
    }
  }
  .chat-example {
    width: 800px;
    margin: 100px auto 0;
    p {
      text-align: center;
      color: #909399;
      font-size: 14px;
    }
    &-item {
      margin-bottom: 24px;
      cursor: pointer;
      .el-card {
        :hover {
          background-color: var(--el-color-primary-light-9);
        }
      }
      &-content {
        font-size: 14px;
        color: #909399;
        margin-top: 10px;
      }
    }
  }

  .chat-input {
    position: relative;

    :deep(.el-textarea__inner) {
      padding-bottom: 10px;
      color: #303133;
    }

    .chat-tools {
      position: absolute;
      bottom: 3px;
      width: 100%;
      display: flex;
      justify-content: end;
      align-items: center;
      padding: 0 5px;
      box-sizing: border-box;
    }
  }
}
.custom-dropdown {
  &:focus-visible {
    outline: unset;
  }
}
</style>
