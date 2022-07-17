/**
 * 系统配置类型
 */
export interface SystemConfigType extends LabelValue<string[]> {
  data?: Record<string, string>
}

/**
 * 系统配置元素
 */
export interface SystemConfig<E = string> {
  type: string
  prop: string
  propLabel: string
  value?: E
  defaultValue?: unknown
  options?: string[]
  required?: boolean

  _edit?: boolean
}
