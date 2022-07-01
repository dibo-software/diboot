export interface IAppStore {
  enableTabs: boolean
  enableFooter: boolean
  globalSize: 'large' | 'default' | 'small'
  layout: 'default' | 'dock' | 'menu' | 'topNav'
  colorPrimary?: string
}

export default defineStore('app', {
  state: (): IAppStore => {
    return {
      enableTabs: true,
      enableFooter: true,
      globalSize: 'default',
      layout: 'default',
      colorPrimary: undefined
    }
  },
  persist: {
    enabled: true,
    strategies: [
      {
        storage: localStorage
      }
    ]
  }
})
