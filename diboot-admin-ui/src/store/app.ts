export interface IAppStore {
  enableTabs: boolean
  enableFooter: boolean
  globalSize: 'large' | 'default' | 'small'
  layout: 'default' | 'dock' | 'menu' | 'topNav'
}

export default defineStore('app', {
  state: (): IAppStore => {
    return {
      enableTabs: true,
      enableFooter: true,
      globalSize: 'default',
      layout: 'default'
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
