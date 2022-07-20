/**
 * 系统配置类型
 */
export interface SystemConfigType extends LabelValue<string[]> {
  data: Record<string, string>
}

/**
 * 系统配置元素
 */
export interface SystemConfig {
  type: string
  prop: string
  propLabel: string
  value?: string
  defaultValue?: unknown
  options?: string[]
  required?: boolean

  _edit?: boolean
}
