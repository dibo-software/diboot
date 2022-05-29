export interface IAppStore {
  enableTabs: boolean
  globalSize: 'large' | 'default' | 'small'
  layout: 'default' | 'dock' | 'menu' | 'topNav'
  colorPrimary?: string
}

export default defineStore('app', {
  state: (): IAppStore => {
    return {
      layout: 'default',
      globalSize: 'default',
      enableTabs: true,
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
