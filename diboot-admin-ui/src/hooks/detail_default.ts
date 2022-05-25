/**
 * 获取详情
 *
 * @param baseApi
 * @param init 初始值
 */
export default <T>(baseApi: string, init: Partial<T> = {}) => {
  const loading = ref(false)
  const model = ref<Partial<T>>(init)
  const error = ref()

  const loadData = (id?: string) => {
    // 在请求之前重设状态...
    model.value = init
    error.value = undefined

    if (!id) return

    loading.value = true

    api
      .get<T>(`${baseApi}/${unref(id)}`)
      .then(res => (model.value = res.data ?? init))
      .catch(err => {
        error.value = err
        ElMessage.error(err.msg ?? err.message ?? '获取详情失败')
      })
      .finally(() => (loading.value = false))
  }

  return { loadData, loading, model, error }
}
