import auth from '@/utils/auth'
import router, { resetRouter } from '@/router'

export interface IAuthStore {
  realname: string
  avatar?: string
  roles: Array<string>
  info?: IUserInfo
}

export interface IUserInfo extends Record<string, unknown> {
  realname?: string
  avatar?: string
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
          .post<string>('/auth/login', account)
          .then(res => {
            if (res.data) {
              auth.setToken(res.data)
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
        const res = await api.get<{ info: IUserInfo; roles: Array<string> }>('/auth/user-info')
        const BASE_URL = import.meta.env.VITE_APP_BASE_URL
        this.info = res.data?.info
        this.avatar = `${BASE_URL}${this.info?.avatarUrl}/image`
        this.realname = `${this.info?.realname}`
        this.roles = res.data?.roles ?? []
      } catch (e) {
        throw new Error('获取登录用户信息异常')
      }
    },
    getNewInfo: async function () {
      try {
        const res = await api.get<{ info: IUserInfo; roles: Array<string> }>('/iam/user/current-user-info')
        const BASE_URL = import.meta.env.VITE_APP_BASE_URL
        this.info = res.data
        this.avatar = `${BASE_URL}${this.info?.avatarUrl}/image`
        this.realname = `${this.info?.realname}`
      } catch (e) {
        throw new Error('获取登录用户信息异常')
      }
    },
    async logout() {
      try {
        await api.post('/auth/logout')
      } finally {
        auth.clearToken()
        this.$reset()
        resetRouter()
        router.push({ name: 'Login' }).finally()
      }
    }
  }
})
