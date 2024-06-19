import type { UnwrapRef } from 'vue'
import i18n from '@/i18n'
/**
 * 获取详情
 *
 * @param baseApi
 * @param init 初始值
 */
export default <T>(baseApi: string, init: Partial<T> = {}) => {
  const loading = ref(false)
  const model = ref<Partial<T>>(_.cloneDeep(init))

  const loadData = (id?: string) => {
    // 在请求之前重设状态...
    model.value = _.cloneDeep(init as UnwrapRef<Partial<T>>)

    if (!id) return Promise.resolve()

    loading.value = true

    return new Promise<void>((resolve, reject) => {
      api
        .get<T>(`${baseApi}/${unref(id)}`)
        .then(res => {
          model.value = (res.data ?? {}) as UnwrapRef<Partial<T>>
          resolve()
        })
        .catch(err => {
          reject(err)
          ElMessage.error(err.msg ?? err.message ?? i18n.global.t('hooks.fetchDetailFailed'))
        })
        .finally(() => (loading.value = false))
    })
  }

  return { loadData, loading, model }
}
