const zhCN = {
  i18nConfig: {
    i18nCode: '资源标识',
    translate: '翻译',
    type: '类型',
    content: '翻译内容',
    internationalTranslation: '国际化翻译',
    rules: {
      i18nCode: '只允许字，数字， . ， - ， _'
    },
    fetchDataListError: '获取列表数据失败'
  }
}

export type Locale = typeof zhCN

export default zhCN
