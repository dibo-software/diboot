import { createProdMockServer } from 'vite-plugin-mock/es/createProdMockServer'
import type { MockMethod } from 'vite-plugin-mock'

const modules = import.meta.glob<MockMethod[]>('./**/*.ts', { import: 'default', eager: true })

const mockModules: MockMethod[] = []
Object.keys(modules)
  .filter(path => !path.includes('/_')) // Ignore files and directories starting with _
  .forEach(key => mockModules.push(...modules[key]))

/**
 * Used in a production environment. Need to manually import all modules
 */
export function setupProdMockServer() {
  createProdMockServer(mockModules)
}
