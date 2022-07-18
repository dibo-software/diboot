import type { FormInstance } from 'element-plus'

interface FormOption {
  // 主键属性名（默认值：id）
  primaryKey?: string
  // 基础接口
  baseApi: string
  // 自定义创建接口
  createApi?: string
  // 自定义更新接口
  updateApiPrefix?: string
  // 校验成功后置处理函数
  afterValidate?: () => Promise<void> | void
  // 成功回调
  successCallback: (primaryKey?: string) => void
}

export default (option: FormOption) => {
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

  const submit = async (formEl: FormInstance | undefined, data: Record<string, unknown>) => {
    const { baseApi, primaryKey = 'id', createApi, updateApiPrefix, afterValidate, successCallback } = option
    try {
      confirmSubmit.value = true
      await validate(formEl)
      if (afterValidate) await afterValidate()
      const id = data[primaryKey]
      let res
      if (id) {
        res = await api.put<never>(updateApiPrefix ? `${updateApiPrefix}/${id}` : `${baseApi}/${id}`, data)
      } else {
        res = await api.post<string>(createApi ? createApi : baseApi, data)
      }
      ElMessage.success(res.msg)
      successCallback(res.data)
    } catch (e: any) {
      ElMessage.error(e.msg || e.message || (e.length ? e : '提交失败'))
    } finally {
      confirmSubmit.value = false
    }
  }

  return {
    confirmSubmit,
    submit
  }
}
