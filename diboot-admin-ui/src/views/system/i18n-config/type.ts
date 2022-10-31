export interface I18nConfig {
  id: string
  // 类型
  type: string
  // 类型显示值
  typeLabel?: LabelValue<{ color?: string }>
  // 语言
  language: string
  //消息代码
  code: string
  // 内容
  content: string
  // 创建时间
  createTime?: string
}
