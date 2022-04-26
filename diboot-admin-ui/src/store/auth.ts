import auth from '@/utils/auth'
import { ElMessage } from 'element-plus'

interface IAuthStore {
  realname: string
  email: string
  avatar: string
  info: any
}

export default defineStore('auth', {
  state: () => {
    return <IAuthStore>{
      realname: '',
      avatar: '',
      info: null
    }
  },
  actions: {
    login(account: any) {
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
        const res = await api.get<{ realname: string; avatar: string }>('/auth/userInfo')
        this.info = res.data
        this.avatar = `${res.data?.avatar}`
        this.realname = `${res.data?.realname}`
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
      }
    }
  }
})
