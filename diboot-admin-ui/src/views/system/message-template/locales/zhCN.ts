const zhCN = {
  messageTemplate: {
    title: '模版标题',
    code: '模版编码',
    appModule: '业务模块',
    content: '内容',
    placeholder: {
      title: '消息模板名称，如：‘催办提醒’',
      code: '模板唯一编码，如：‘CBTX’',
      appModule: '请选择消息状态',
      content: '发送日期'
    },
    selectableVariables: '可选变量',
    addVariable: '添加此变量'
  }
}

export type Locale = typeof zhCN

export default zhCN
