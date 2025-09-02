import auth from '@/utils/auth'
import router, { resetRouter } from '@/router'
import { buildImgSrc } from '@/utils/file'
import type { UserModel } from '@/views/org-structure/user/type'
import type { Role } from '@/views/system/role/type'
import useViewTabs from './view-tabs'
import i18n from '@/i18n'

export interface IAuthStore {
  realname: string
  avatar?: string
  roles: Array<Role>
  positions: Array<LabelValue>
  curPosition?: LabelValue
  info?: UserModel
}

export default defineStore('auth', {
  state: () => {
    return <IAuthStore>{
      realname: '',
      avatar: undefined,
      roles: [],
      positions: [],
      curPosition: undefined,
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
    getInfo: async function (refresh = false) {
      try {
        const res = await api.get<{
          info: UserModel
          roles: Array<Role>
          curPosition: LabelValue
          positions: Array<LabelValue>
        }>('/auth/user-info', { refresh })
        this.info = res.data?.info
        if (this.info?.avatarUrl) this.avatar = buildImgSrc(this.info?.avatarUrl)
        this.realname = `${this.info?.realname}`
        this.roles = res.data?.roles ?? []
        this.positions = res.data?.positions ?? []
        this.curPosition = res.data?.curPosition ?? {}

        if (import.meta.env.VITE_APP_ENABLE_I18N === 'true') {
          api
            .get('/i18n-config/all')
            .then(res => i18n.global.mergeLocaleMessage(unref(i18n.global.locale), res.data ?? {}))
            .catch(err => console.error(err.msg || err.message))
        }
      } catch (e) {
        throw new Error('获取登录用户信息异常', e)
      }
    },
    /**
     * 切换岗位
     * @param position
     */
    async switchPosition(position: LabelValue) {
      try {
        await api.post('/auth/switch-position', position)
      } finally {
        window.location.reload()
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
        useViewTabs().$reset()
      }
    }
  }
})
