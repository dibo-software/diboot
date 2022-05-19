import { ElButton } from 'element-plus'

export interface ListOption<T> {
  // 请求接口基础路径
  baseApi: string
  // 列表数据接口
  listApi?: string
  // 自定义参数（不被查询表单重置和改变的参数）
  initQueryParam?: QueryParam<T>
  // 重建查询条件
  rebuildQuery?: (query: QueryParam<T>) => QueryParam<T>
}

export type QueryParam<T> = Partial<T> & Record<string, unknown>

export interface Pagination {
  pageSize: number
  current: number
  total: number
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

  const queryParam: QueryParam<D> = reactive(_.cloneDeep(option.initQueryParam ?? {}))

  const dateRangeQuery: Record<string, [string | number | Date, string | number | Date]> = reactive({})

  /**
   * 构建查询参数
   */
  const buildQueryParam = () => {
    // 合并自定义查询
    const tempQueryParam: QueryParam<unknown> = _.cloneDeep(queryParam)
    // 合并分页参数
    tempQueryParam.pageIndex = pagination.current
    tempQueryParam.pageSize = pagination.pageSize
    // 合并日期范围查询参数
    for (const [key, value] of Object.entries(dateRangeQuery)) {
      tempQueryParam[`${key}Begin`] = value[0]
      tempQueryParam[`${key}End`] = value[1]
    }
    // TODO 日期格式化

    // 改造查询条件（用于列表页扩展）
    return option.rebuildQuery ? option.rebuildQuery(tempQueryParam) : tempQueryParam
  }

  /**
   * 获取数据列表
   */
  const getList = () => {
    loading.value = true
    api
      .get<Array<T>>(option.listApi ? option.listApi : `${option.baseApi}/list`, buildQueryParam())
      .then(res => {
        dataList.splice(0, dataList.length)
        if (res.data) dataList.push(...(res.data ?? []))
        pagination.pageSize = res.page?.pageSize
        pagination.current = res.page?.pageIndex
        pagination.total = res.page?.totalCount ? Number(res.page.totalCount) : 0
      })
      .catch(err => {
        ElNotification.error({
          title: '获取列表数据失败',
          message: err.msg || err.message
        })
      })
      .finally(() => (loading.value = false))
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
    Object.keys(queryParam).forEach(key => delete queryParam[key])
    Object.assign(queryParam, _.cloneDeep(option.initQueryParam ?? {}))
    onSearch()
  }

  // 删除
  const del = useDelete({ deleteCallback: getList, ...option })

  return {
    queryParam,
    dateRangeQuery,
    onSearch,
    resetFilter,
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
  // 是否允许撤回删除 (默认允许)
  allowCanceledDelete?: boolean
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
    ElMessageBox.confirm('确认删除该数据吗？', '删除', { type: 'warning' })
      .then(() => {
        api
          .delete(`${option.baseApi}${option.deleteApiPrefix ?? ''}/${id}`)
          .then(() => removeSuccessHandler([id]))
          .catch(err => {
            ElMessage.error(err.msg || err.message || '删除失败')
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
      ElMessage.warning('未选择数据')
      return
    }

    ElMessageBox.confirm('确认删除已选数据吗？', '批量删除', { type: 'warning' })
      .then(() => {
        api
          .post(`${option.baseApi}/batchDelete`, ids)
          .then(() => removeSuccessHandler(ids))
          .catch(err => {
            ElMessage.error(err.msg || err.message || '删除失败')
          })
      })
      .catch(() => null)
  }

  /**
   * 删除成功处理
   *
   * @param ids
   */
  const removeSuccessHandler = (ids: Array<string>) => {
    if (option.deleteCallback) option.deleteCallback()

    if (option.allowCanceledDelete === false) {
      ElMessage.success('数据删除成功')
      return
    }

    // 支持撤回的删除成功提示
    const message = ElMessage.success({
      type: 'success',
      message: h('span', null, [
        h('span', { style: { color: 'var(--el-color-success)' } }, '数据删除成功'),
        h(
          ElButton,
          {
            bg: true,
            text: true,
            type: 'primary',
            style: { height: '25px', position: 'absolute', right: '13px' },
            onClick: () => {
              message.close()
              api
                .patch(`${option.baseApi}/cancelDeleted`, ids)
                .then(() => {
                  ElMessage.success('撤回成功')
                  if (option.deleteCallback) option.deleteCallback()
                })
                .catch(() => ElMessage.error('撤回失败'))
            }
          },
          { default: () => '撤回' }
        )
      ])
    })
  }

  return { remove, batchRemove }
}
