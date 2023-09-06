import type { FormInstance } from 'vant'

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
  successCallback: (primaryKey: string) => void
}

export default (option: FormOption) => {
  // 提交表单状态
  const submitting = ref(false)

  /**
   * 提交数据
   *
   * @param data
   * @param formEl
   */
  const submit = async (data: Record<string, unknown>, formEl?: FormInstance) => {
    const { baseApi, primaryKey = 'id', createApi, updateApiPrefix, afterValidate, successCallback } = option
    try {
      submitting.value = true
      await formEl?.validate()
      if (afterValidate) await afterValidate()
      const id = data[primaryKey]
      let res
      if (id) {
        res = await api.put<never>(updateApiPrefix ? `${updateApiPrefix}/${id}` : `${baseApi}/${id}`, data)
      } else {
        res = await api.post<string>(createApi ? createApi : baseApi, data)
      }
      showNotify({ type: 'success', message:res.msg })
      successCallback(res.data ?? (id as string))
      return true
    } catch (e: any) {
      showNotify({ type: 'danger', message: e.msg || e.message || (e.length ? e : '保存失败') })
      return false
    } finally {
      submitting.value = false
    }
  }

  /**
   * Post提交数据
   *
   * @param data
   * @param formEl
   */
  const submitPost = async (data: unknown, formEl?: FormInstance) => {
    const { baseApi, afterValidate, successCallback } = option
    try {
      submitting.value = true
      await formEl?.validate()
      if (afterValidate) await afterValidate()
      const res = await api.post<string>(baseApi, data)
      showNotify({ type: 'success', message:res.msg })
      successCallback(res.data)
      return true
    } catch (e: any) {
      showNotify({ type: 'danger', message: e.msg || e.message || (e.length ? e : '保存失败') })
      return false
    } finally {
      submitting.value = false
    }
  }

  return {
    submitting,
    submit,
    submitPost
  }
}
