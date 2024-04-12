/**
 * 表单校验属性值
 *
 * @param validateApi 校验接口
 * @param prop 属性
 * @param id 数据ID
 * @param param 额外的参数
 */
export const checkValue =
  (validateApi: string, prop: string, id: () => string | undefined, param?: () => Record<string, unknown>) =>
  (rule: unknown, value: unknown, callback: (error?: string | Error) => void) => {
    if (value) {
      const params: Record<string, unknown> = (param && param()) ?? {}
      params.id = id()
      params[prop] = value
      api
        .get<boolean | undefined>(validateApi, params)
        .then(res => callback(res.data !== false ? void 0 : `内容重复，${value} 已存在!`))
        .catch(err => callback(err.msg || err))
    } else callback()
  }
