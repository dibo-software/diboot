import auth from '@/utils/auth'
import { resetRouter } from '@/router'

export interface IAuthStore {
  realname: string
  avatar?: string
  roles: Array<string>
  info?: IUserInfo
}

export interface IUserInfo extends Record<string, unknown> {
  realname?: string
  avatar?: string
  roles: Array<string>
}

export default defineStore('auth', {
  state: () => {
    return <IAuthStore>{
      realname: '',
      avatar: undefined,
      roles: [],
      info: undefined
    }
  },
  actions: {
    login(account: unknown) {
      return new Promise((resolve, reject) => {
        api
          .post<{ token: string }>('/auth/login', account)
          .then(res => {
            if (res.data) {
              auth.setToken(res.data.token)
              resolve(res.data)
            }
          })
          .catch(err => {
            ElMessage.error(err.message || err.msg || '稍后重试')
            reject()
          })
      })
    },
    getInfo: async function () {
      try {
        const res = await api.get<{ realname: string; avatar: string; roles: Array<string> }>('/auth/userInfo')
        this.info = res.data
        this.avatar = `${res.data?.avatar}`
        this.realname = `${res.data?.realname}`
        this.roles = res.data?.roles ?? []
      } catch (e) {
        throw new Error('获取登录者信息异常')
      }
    },
    async logout() {
      try {
        await api.post('/auth/logout')
      } finally {
        auth.clearToken()
        this.$reset()
        resetRouter()
      }
    }
  }
})
