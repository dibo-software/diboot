import type { FormInstance } from 'element-plus'

interface FormOption<T> {
  // 主键属性名（默认值：id）
  primaryKey?: string
  // 基础接口
  baseApi: string
  // 自定义创建接口
  createApi?: string
  // 自定义更新接口
  updateApiPrefix?: string
  // 校验成功后置处理函数(多用于其他附加校验处理)
  afterValidate?: () => Promise<void>
  // 提交前数据增强函数(多用于附加数据添加)
  enhance?: (values: T) => Promise<T>
  // 成功回调
  successCallback: (primaryKey?: string) => void
}

export default <T>(option: FormOption<T>) => {
  const confirmSubmit = ref(false)

  const validate = (formEl: FormInstance | undefined) => {
    if (!formEl) return
    return new Promise((resolve, reject) => {
      formEl.validate((valid, fields) => {
        if (valid) {
          resolve(fields)
        } else {
          reject('校验不通过')
        }
      })
    })
  }

  const submit = async (formEl: FormInstance | undefined, data: T) => {
    try {
      const { baseApi, createApi, updateApiPrefix, afterValidate, successCallback } = option
      let { primaryKey } = option
      await validate(formEl)
      if (afterValidate !== undefined) {
        await afterValidate()
      }
      primaryKey = primaryKey || 'id'
      const id: unknown = (data as any)[primaryKey]
      let res
      if (id) {
        res = await api.put<never>(updateApiPrefix ? `${updateApiPrefix}/${id}` : `${baseApi}/${id}`, data)
      } else {
        res = await api.post<string>(createApi ? createApi : baseApi, data)
      }
      ElMessage.success(res.msg)
      successCallback(res.data)
    } catch (e: any) {
      ElMessage.error(e.msg || e.message || e.length ? e : '提交失败')
    } finally {
      confirmSubmit.value = false
    }
  }

  return {
    confirmSubmit,
    submit
  }
}
