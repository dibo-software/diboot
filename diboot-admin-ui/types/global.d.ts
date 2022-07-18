declare global {
  export interface LabelValue<E = never> {
    // 存储值
    value: string
    // 显示值
    label: string
    // 扩展值
    ext?: E

    // 对象类型
    type?: string
    // 是否为叶子节点
    leaf?: boolean
    // 是否禁用
    disabled?: boolean
    // 子节点集合
    children?: LabelValue<E>
  }

  /**
   * 文件记录
   */
  interface FileRecord {
    uuid: string
    // 应用模块
    appModule?: string
    // MD5标识
    md5?: string
    // 文件名称
    fileName: string
    // 文件类型
    fileType: string
    // 文件大小（Byte）
    fileSize: string
    // 访问地址
    accessUrl: string
    // 缩略图地址
    thumbnailUrl?: string
    // 备注
    description?: string
    // 创建人
    createByName: string
    // 创建时间
    createTime: string
  }
}

export {}
