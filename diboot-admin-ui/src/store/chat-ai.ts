import { api } from '@/utils/request'
import hljs from 'highlight.js'

type RoleType = 'system' | 'user' | 'assistant'
type AiMessage = {
  role: RoleType
  content: string
}

export interface AiSession {
  id?: string
  title: string
  edit?: boolean
  sessionRecordList?: AiSessionRecord[]
  createTime?: string
}

export interface IChatAiStore {
  sessions: AiSession[]
  currentSession?: AiSession
  currentMessages?: AiMessage[]
}

export interface AiSessionRecord {
  sessionId: string
  model: string
  requestBody: string
  responseBody: string
}
export default defineStore('chatAi', {
  state: (): IChatAiStore => {
    return {
      // 会话列表
      sessions: [],
      // 当前会话
      currentSession: undefined,
      // 当前会话消息
      currentMessages: []
    }
  },
  actions: {
    async beforeSendMessage(message: string) {
      // 判断session是否存在，不存在，则需要先创建session
      if (!this.currentSession) {
        const newSession = await this.createSession(message)
        if (!newSession) {
          ElMessage.error('创建会话失败')
          return
        }
        this.currentSession = newSession
        this.getSessions()
      }
    },
    /**
     * 选中session
     * @param session
     */
    chooseSession(session: AiSession) {
      this.currentSession = session
    },
    /**
     * 获取session列表
     */
    getSessions() {
      return new Promise((resolve, reject) => {
        api
          .get<AiSession>('/ai-session/list')
          .then(res => {
            this.sessions = res.data || []
          })
          .catch(err => {
            reject()
          })
      })
    },
    /**
     * 创建session会话
     */
    async createSession(title: string) {
      return new Promise((resolve, reject) => {
        api
          .post<string>('/ai-session', {
            title
          })
          .then(res => {
            if (res.data) {
              this.currentSession = res.data
              resolve(res.data)
            } else reject()
          })
          .catch(err => {
            reject()
          })
      })
    },
    /**
     * 更新会话
     */
    async updateSession(session: AiSession) {
      const res = await api.put<boolean>(`/ai-session/${session.id}`, session)
      if (res.data) ElMessage.success('会话更新成功！')
    },
    /**
     * 移除会话
     * /chat-ai/session/remove
     */
    async removeSession(sessionId: string) {
      // 移除会话链接
      const res = await api.delete<boolean>(`/ai-session/${sessionId}`)
      if (res.code === 0) ElMessage.success('会话移除成功！')
    },

    /**
     * 加载指定session的记录
     */
    async loadSessionRecords(sessionId: string) {
      const res = await api.get<AiMessage[]>(`/ai-session-record/list-by-session-id/${sessionId}`)
      if (res.code === 0) {
        this.currentMessages = res.data
        await nextTick()
        // 含有代码，最后统一刷新高亮
        const blocks = document.querySelectorAll('pre code')
        blocks.forEach(block => {
          hljs.highlightBlock(block)
        })
      }
    }
  }
})
