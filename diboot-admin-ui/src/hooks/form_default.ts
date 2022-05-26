import { FormInstance } from 'element-plus'

interface FormOption {
  // 主键属性名（默认值：id）
  primaryKey?: string
  // 基础接口
  baseApi: string
  // 自定义创建接口
  createApi?: string
  // 自定义更新接口
  updateApiPrefix?: string
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
    try {
      await validate(formEl)

      const { primaryKey, baseApi, createApi, updateApiPrefix, successCallback } = option
      const id: unknown = data[primaryKey ?? 'id']
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
