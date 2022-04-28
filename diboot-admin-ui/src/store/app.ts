export interface IAppStore {
  enableTabs: boolean
  enableFooter: boolean
  globalSize: 'large' | 'default' | 'small'
}

export default defineStore('app', {
  state: (): IAppStore => {
    return {
      enableTabs: true,
      enableFooter: true,
      globalSize: 'default'
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
