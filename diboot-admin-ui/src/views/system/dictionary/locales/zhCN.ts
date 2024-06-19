const zhCN = {
  dictionary: {
    itemName: '字典名称',
    type: '字典编码',
    description: '字典备注',
    item: {
      label: '字典条目',
      itemName: '条目名称',
      itemValue: '条目编码',
      color: '条目颜色',
      internationalization: '国际化',
      sort: '排序',
      rules: {
        itemName: '请输入条目名称',
        itemValue: '请输入条目编码'
      }
    },
    rules: {
      itemName: '请输入字典名称',
      type: '请输入字典编码'
    },
    paramsError: '参数错误'
  }
}

export type Locale = typeof zhCN

export default zhCN
