import auth from '@/utils/auth'
import { ElMessage } from 'element-plus'

interface IUserInfo {
  realname: string
  email: string
  avatar: string
}

export default defineStore('user', {
  state: () => {
    return <IUserInfo>{
      realname: '游客',
      avatar: ''
    }
  },
  actions: {
    login: (account: any) => {
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
    getInfo: () => {
      return new Promise((resolve, reject) => {
        api
          .get('/auth/userInfo')
          .then(res => {
            if (res.data) resolve(res.data)
            else reject()
          })
          .catch(err => {
            reject(err)
          })
      })
    },
    logout: () => {
      // this.realname = 'asd'
      api.post('/auth/logout').finally(() => {
        auth.cleanToken()
      })
    }
  }
})
