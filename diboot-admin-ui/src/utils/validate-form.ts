/**
 * 表单校验属性值
 *
 * @param validateApi 校验接口
 * @param prop 属性
 * @param id 数据ID
 */
export const checkValue = (validateApi: string, prop: string, id?: () => string | undefined) => {
  return (rule: unknown, value: unknown, callback: (error?: string | Error) => void) => {
    if (value) {
      const params: Record<string, unknown> = { id: id ? id() : undefined }
      params[prop] = value
      api
        .get(validateApi, params)
        .then(() => {
          callback()
        })
        .catch(err => {
          callback(err.msg || err)
        })
    }
  }
}
