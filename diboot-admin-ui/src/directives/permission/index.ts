import type { Directive } from 'vue'
import { checkPermission, checkRole } from '@/utils/permission'

/**
 * 检查权限
 *
 * @modifiers not 取反
 * @modifiers all 全部
 */
export const hasPermission: Directive<HTMLElement, string | Array<string>> = (el, binding) => {
  const { not, all } = binding.modifiers
  if (!checkPermission(binding.value, not, all)) el.remove()
}

/**
 * 检查角色
 *
 * @modifiers not 取反
 * @modifiers all 全部
 */
export const hasRole: Directive<HTMLElement, string | Array<string>> = (el, binding) => {
  const { not, all } = binding.modifiers
  if (!checkRole(binding.value, not, all)) el.remove()
}
