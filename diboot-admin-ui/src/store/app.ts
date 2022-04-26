export interface IAppStore {
  enableTabs: boolean
}

export default defineStore('app', {
  state: () => {
    const tabsState = localStorage.getItem('enableTabs')
    return <IAppStore>{
      enableTabs: tabsState ? tabsState === 'true' : true
    }
  }
})
