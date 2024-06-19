const zhCN = {
  config: {
    propKey: '属性名',
    propValue: '属性值',
    category: '类别',
    dataType: '填写类型',
    dataTypeOptions: {
      text: '文本',
      textarea: '文本域',
      number: '数字',
      boolean: '开关'
    },
    fetchException: '获取配置信息异常',
    saveException: '保存配置信息异常',
    ext: '扩展配置'
  }
}

export type Locale = typeof zhCN

export default zhCN
