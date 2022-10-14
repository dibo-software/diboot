export interface MessageTemplate {
  id: string
  // 应用模块
  appModule?: string
  // 模板编码
  code?: string
  // 标题
  title: string
  // 内容
  content: string
  createBy?: string
  createByName?: string
  // 创建时间
  createTime: string
  // 更新时间
  updateTime: string
}
