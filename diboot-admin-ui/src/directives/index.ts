import { App } from 'vue'
import { hasRole, hasPermission } from './permission'

export default (app: App) => {
  app.directive('hasRole', hasRole)
  app.directive('hasPermission', hasPermission)
}
