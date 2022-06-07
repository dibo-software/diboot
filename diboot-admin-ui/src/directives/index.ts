import type { App } from 'vue'
import { hasRole, hasPermission } from './permission'

// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
export default ({ directive }: App) => {
  directive('hasRole', hasRole)
  directive('hasPermission', hasPermission)
}
