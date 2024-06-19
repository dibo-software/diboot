/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  // eslint-disable-next-line @typescript-eslint/no-explicit-any, @typescript-eslint/ban-types
  const component: DefineComponent<{}, {}, any>
  export default component
}

interface ImportMetaEnv {
  readonly VITE_PORT: string
  readonly VITE_OPEN: string
  readonly VITE_APP_BASE_URL: string
  readonly VITE_APP_ENABLE_TENANT: string
  readonly VITE_APP_ENABLE_I18N: string
  readonly VITE_IFRAME_ORIGIN_LIST: string
}
