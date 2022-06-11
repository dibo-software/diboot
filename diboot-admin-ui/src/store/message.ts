export interface MessageStore {
  show: boolean
  list: MessageInfo[]
}

export interface MessageInfo {
  id: string
  new?: boolean
  source: string
  title: string
  content: string
  time: string
}

export default defineStore('message', {
  state: (): MessageStore => {
    return {
      show: false,
      list: [
        {
          id: '0',
          new: true,
          source: 'Diboot',
          title: '欢迎使用 diboot-admin-ui',
          content:
            '一个基于 Vue.js 3.x & TypeScript & Vite 的开箱即用的中后台管理系统；包含基本的身份认证和鉴权，DevTools 代码自动生成。',
          time: '2022-06-10 18:12:22'
        }
      ]
    }
  },
  actions: {
    async loadNewMessages() {
      const { data } = await api.get<MessageInfo[]>('/message/new')
      if (data) {
        data.map(e => (e.new = true))
        this.list.unshift(...data)
      }
    },
    add(message: MessageInfo) {
      this.list.unshift(message)
    },
    remove(id: string) {
      api.patch(`/message/read/${id}`).finally(() => ({}))
      const index = this.list.findIndex(e => e.id === id)
      return index >= 0 ? !!this.list.splice(index, 1) : false
    }
  }
})
