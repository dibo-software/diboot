import i18n from '@/i18n'
export interface ListOption<D> {
  // 请求接口基础路径
  baseApi: string
  // 列表数据接口
  listApi?: string
  // 初始化参数
  initQueryParam?: Partial<D>
  // 重建查询条件
  rebuildQuery?: (query: Partial<D>) => Partial<D>
  // 列表数据加载成功后执行函数
  loadSuccess?: () => void
}

export interface Pagination {
  pageSize: number
  current: number
  total: number
  orderBy?: string
}

/**
 * 列表操作
 *
 * @param option
 */
export default <T, D = T>(option: ListOption<D> & DeleteOption) => {
  // 标记加载状态
  const loading = ref(false)

  const dataList: Array<T> = reactive([])

  const pagination: Partial<Pagination> = reactive({})

  const queryParam: Partial<D> = reactive(_.cloneDeep(option.initQueryParam ?? {}))

  const dateRangeQuery: Record<string, [string | number | Date, string | number | Date]> = reactive({})

  /**
   * 构建查询参数
   */
  const buildQueryParam = () => {
    const tempQueryParam: Record<string, unknown> = _.cloneDeep(queryParam)
    // 合并分页、排序参数
    tempQueryParam.pageIndex = pagination.current
    tempQueryParam.pageSize = pagination.pageSize
    tempQueryParam.orderBy = pagination.orderBy
    // 合并日期范围查询参数
    for (const [key, value] of Object.entries(dateRangeQuery)) {
      if (value) [tempQueryParam[`${key}Begin`], tempQueryParam[`${key}End`]] = value
    }
    // TODO 日期格式化

    // 改造查询条件（用于列表页扩展）
    return option.rebuildQuery ? option.rebuildQuery(tempQueryParam as Partial<D>) : tempQueryParam
  }

  /**
   * 获取数据列表
   */
  const getList = () => {
    loading.value = true

    return new Promise<void>((resolve, reject) => {
      api
        .get<Array<T>>(option.listApi ? option.listApi : option.baseApi, buildQueryParam())
        .then(res => {
          dataList.length = 0
          dataList.push(...(res.data || []))
          const { pageSize, pageIndex, totalCount, orderBy } = res.page ?? {}
          pagination.pageSize = pageSize
          pagination.current = pageIndex
          pagination.total = totalCount ? Number(totalCount) : 0
          pagination.orderBy = orderBy
          if (option.loadSuccess !== undefined) option.loadSuccess()
          resolve()
        })
        .catch(err => {
          ElNotification.error({
            title: i18n.global.t('hooks.fetchListFailed'),
            message: err.msg || err.message || err
          })
          reject(err)
        })
        .finally(() => (loading.value = false))
    })
  }

  /**
   * 搜索，查询第一页
   */
  const onSearch = _.debounce(() => {
    pagination.current = 1
    getList()
  }, 300)

  /**
   * 重置筛选条件
   */
  const resetFilter = () => {
    Object.keys(dateRangeQuery).forEach(key => delete dateRangeQuery[key])
    Object.keys(queryParam).forEach(key => delete queryParam[key as keyof D])
    Object.assign(queryParam, option.initQueryParam ?? {})
    onSearch()
  }

  // 删除
  const del = useDelete({ deleteCallback: getList, ...option })

  return {
    queryParam,
    dateRangeQuery,
    onSearch,
    resetFilter,
    buildQueryParam,
    getList,
    loading,
    dataList,
    pagination,
    ...del
  }
}

// 删除数据
export interface DeleteOption {
  // 请求接口基础路径
  baseApi: string
  // 删除数据接口前缀
  deleteApiPrefix?: string
  // 删除回调
  deleteCallback?: () => void
}

/**
 * 删除数据
 *
 * @param option
 */
export const useDelete = (option: DeleteOption) => {
  /**
   * 删除数据
   *
   * @param id
   */
  const remove = (id: string) => {
    return ElMessageBox.confirm(i18n.global.t('hooks.confirmDelete'), i18n.global.t('hooks.delete'), {
      type: 'warning'
    })
      .then(() => {
        return api
          .delete(`${option.baseApi}${option.deleteApiPrefix ?? ''}/${id}`)
          .then(() => {
            removeSuccessHandler()
            return true
          })
          .catch(err => {
            ElMessage.error(err.msg || err.message || i18n.global.t('hooks.deleteFailed'))
          })
      })
      .catch(() => null)
  }

  /**
   * 批量删除数据
   *
   * @param ids
   */
  const batchRemove = (ids: Array<string>) => {
    if (!(ids && ids.length)) {
      ElMessage.warning(i18n.global.t('hooks.nonChooseData'))
      return Promise.resolve()
    }

    return ElMessageBox.confirm(i18n.global.t('hooks.confirmBatchDelete'), i18n.global.t('hooks.batchDelete'), {
      type: 'warning'
    })
      .then(() => {
        return api
          .post(`${option.baseApi}/batch-delete`, ids)
          .then(() => {
            removeSuccessHandler()
            return true
          })
          .catch(err => {
            ElMessage.error(err.msg || err.message || i18n.global.t('hooks.deleteFailed'))
          })
      })
      .catch(() => null)
  }

  /**
   * 删除成功处理
   */
  const removeSuccessHandler = () => {
    if (option.deleteCallback) option.deleteCallback()
    ElMessage.success(i18n.global.t('hooks.deleteSuccess'))
  }

  return { remove, batchRemove }
}
