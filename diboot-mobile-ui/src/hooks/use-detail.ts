/**
 * 获取详情
 *
 * @param baseApi
 * @param init 初始值
 */
export default <T>(baseApi: string, init: Partial<T> = {}) => {
  const loading = ref(false)
  const model = ref<Partial<T>>(init)

  const loadData = (id?: string) => {
    // 在请求之前重设状态...
    model.value = init

    if (!id) return Promise.resolve()

    loading.value = true

    return new Promise<void>((resolve, reject) => {
      api
        .get<T>(`${baseApi}/${unref(id)}`)
        .then(res => {
          model.value = res.data
          resolve()
        })
        .catch(err => {
          reject(err)
          showNotify({ type: 'danger', message: err.msg ?? err.message ?? '获取详情失败' });
        })
        .finally(() => (loading.value = false))
    })
  }

  return { loadData, loading, model }
}
