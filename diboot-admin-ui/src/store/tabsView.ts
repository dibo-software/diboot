import { RouteLocationNormalizedLoaded } from 'vue-router'

interface ITagsViewStore {
  visitedViews: RouteLocationNormalizedLoaded[]
  cachedViews: string[]
}

export default defineStore('tags-view', {
  state() {
    return <ITagsViewStore>{
      visitedViews: [],
      cachedViews: []
    }
  },
  actions: {
    addView(view: RouteLocationNormalizedLoaded) {
      if (!this.visitedViews.includes(view)) {
        return
      }
      this.visitedViews.push(view)
      if (view.meta.keepAlive !== false) this.cachedViews.push(view.name as string)
    },
    delView(view: RouteLocationNormalizedLoaded) {
      console.log(view)
    }
  }
})
