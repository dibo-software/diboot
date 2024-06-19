import i18n from '@/i18n'
interface SsoOption {
  callback: (token: string) => void
}
interface AuthorizeInfo {
  url: string
  state?: string
}

export default (option: SsoOption) => {
  const SSO_TYPE = 'ssoType'
  const SSO_STATE = 'ssoState'
  // 获取单点登录URL Map
  const ssoLoading = ref(false)
  const authorizeInfoMap = ref<Record<string, AuthorizeInfo>>({})
  const loadAuthorizeInfoMap = async () => {
    const res = await api.get('/auth/sso/load-authorize-info-map')
    if (res.code === 0) {
      authorizeInfoMap.value = res.data
    }
  }
  const buildParamsByPath = (): Record<string, string> => {
    const params = new URLSearchParams(window.location.search)
    const paramObject: Record<string, string> = {}
    // 遍历参数并将其存储到对象中
    for (const [key, value] of params.entries()) {
      paramObject[key] = value
    }

    return paramObject
  }
  const ssoLogin = async (params: Record<string, string>) => {
    ssoLoading.value = true
    try {
      const res = await api.post('/auth/sso/auth-check', params)
      if (res.code === 0 && res.data) {
        option.callback(res.data as string)
      } else {
        ElMessage.success(res.msg || i18n.global.t('hooks.loginFailed'))
      }
    } finally {
      ssoLoading.value = false
    }
  }
  const initSsoParams = () => {
    // 获取是否为重定向链接
    const ssoType = sessionStorage.getItem(SSO_TYPE)
    const ssoState = sessionStorage.getItem(SSO_STATE)
    if (ssoType) {
      try {
        const params = buildParamsByPath()
        params.authType = ssoType
        params.state = ssoState || ''
        ssoLogin(params)
      } finally {
        sessionStorage.removeItem(SSO_TYPE)
        sessionStorage.removeItem(SSO_STATE)
      }
    } else {
      loadAuthorizeInfoMap()
    }
  }
  // 重定向到单点登录地址
  const redirectTo = (ssoType: string) => {
    if (!ssoType) {
      ElMessage.warning(i18n.global.t('hooks.nullSsoLoginUrl'))
      return
    }
    if (!authorizeInfoMap.value[ssoType]) {
      ElMessage.warning(i18n.global.t('hooks.nullSsoLoginUrl'))
      return
    }
    const info: AuthorizeInfo = authorizeInfoMap.value[ssoType]
    // sessionStorage中记录登录方式，与state信息
    sessionStorage.setItem(SSO_TYPE, ssoType)
    sessionStorage.setItem(SSO_STATE, info.state || '')
    location.href = info.url
  }
  return { ssoLoading, authorizeInfoMap, initSsoParams, redirectTo }
}
