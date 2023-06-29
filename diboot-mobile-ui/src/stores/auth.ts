import auth from '@/utils/auth'
import router from '@/router'

export interface IAuthStore {
  realname: string
  avatar?: string
  roles: Array<any>
  info?: any
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
            showFailToast(err.message || err.msg || '稍后重试')
            reject()
          })
      })
    },
    getInfo: async function (refresh = false) {
      try {
        const res = await api.get<{ info: any; roles: Array<any> }>('/auth/user-info', { refresh })
        this.info = res.data?.info
        if (this.info?.avatarUrl) this.avatar = 'buildImgSrc(this.info?.avatarUrl)'
        this.realname = `${this.info?.realname}`
        this.roles = res.data?.roles ?? []
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
        router.push({ name: 'Login' }).finally()
      }
    }
  }
})
