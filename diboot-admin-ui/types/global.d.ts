declare global {
  export interface LabelValue<E = never> {
    // 存储值
    value: string
    // 显示值
    label: string
    // 扩展值
    ext?: E

    // tree构建数据的parentId值（整tree获取或远程搜索tree节点时有值）
    parentId?: string
    // 子节点集合
    children?: LabelValue<E>[]
  }

  /**
   * 文件记录
   */
  interface FileRecord {
    id: string
    // 应用模块
    appModule?: string
    // MD5标识
    md5?: string
    // 文件名称
    fileName: string
    // 文件类型
    fileType: string
    // 文件大小
    fileSizeLabel: string
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
