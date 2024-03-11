import useAuthStore from '@/store/auth'
import auth from '@/utils/auth'
import { AUTH_HEADER_KEY } from '@/utils/auth'
import { isDark, colorPrimary } from '@/utils/theme'

export const loadIframeOriginList = () => {
  const iframeOriginListStr: string = import.meta.env.VITE_IFRME_ORIGIN_LIST
  let iframeOriginList: string[] = [window.origin]
  if (iframeOriginListStr) {
    iframeOriginList = iframeOriginListStr.split(',')
  }
  return iframeOriginList
}

const buildIframeIdSelector = (origin: string) => {
  return `#iframe_${origin
    .replace('://', '_')
    .replace(':', '_')
    .replace(/\./g, '_')
    .replace(/\//g, '')
    .replace('#', '_')}`
}

export const initFunction = async () => {
  const iframeOriginList = loadIframeOriginList()
  // 捕获iframe消息
  window.addEventListener('message', (event: any) => {
    // 判断消息来源是否合法
    if (iframeOriginList.includes(event.origin)) {
      // 请求认证token
      if (event.data === 'RequestAuthorization') {
        const idSelector = buildIframeIdSelector(event.origin)
        const iframe: HTMLIFrameElement | null = document.querySelector(idSelector)
        // 获取token并返回
        const token = auth.getToken()
        const info = useAuthStore().info
        const result = { type: 'token', token: token, tokenName: AUTH_HEADER_KEY, realname: info?.realname }
        iframe?.contentWindow?.postMessage(result, event.origin)
      }
      // 请求主题数据
      if (event.data === 'RequestThemeInfo') {
        // 广播黑夜模式
        broadcastToAllIframe({
          type: 'dark-mode',
          isDark: isDark.value
        })
        // 广播颜色主题
        broadcastToAllIframe({
          type: 'theme-color',
          color: colorPrimary.value
        })
      }
    }
  })

  // 监听主题变化，随之改变主题，并广播到所有页面
  watch(isDark, value => {
    // 将主题变化传递给所有iframe页面
    broadcastToAllIframe({
      type: 'dark-mode',
      isDark: value
    })
  })
  // 监听主题色变化
  watch(colorPrimary, value => {
    // 将主题色变化传递给所有iframe页面
    broadcastToAllIframe({
      type: 'theme-color',
      color: value
    })
  })
}

export const broadcastToAllIframe = (val: any) => {
  const iframeOriginList = loadIframeOriginList()
  for (const origin of iframeOriginList) {
    const idSelector = buildIframeIdSelector(origin)
    const iframe: HTMLIFrameElement | null = document.querySelector(idSelector)
    iframe?.contentWindow?.postMessage(val, origin)
  }
}
