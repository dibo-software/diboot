declare global {
  /**
   * 数据类型
   */
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
}

export {}
