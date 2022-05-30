import { ElButton, ElNotification } from 'element-plus'

import { reactive } from 'vue'

type HookOptions<T> = {
  pageLoader?: BaseListPageLoader<T>
  options?: ListOptions<T>
  baseApi?: string
  autoLoad?: boolean
}

export interface Pagination {
  pageSize: number
  current: number
  total: number
  showSizeChanger: boolean
  pageSizeOptions: string[]
}

export interface ListOptions<T> {
  baseUrl?: string
  // 主键字段名
  primaryKey?: string
  // 请求接口基础路径
  baseApi?: string
  // 列表数据接口
  listApi?: string
  // 删除接口
  deleteApiPrefix?: string
  // 批量删除接口
  batchDeleteApi?: string
  // 撤回删除接口(批量)
  cancelDeletedApi?: string
  // 导出接口
  exportApi?: string
  // 自定义参数（不被查询表单重置和改变的参数）
  customQueryParam?: Record<string, string | number | undefined | never>
  // 与查询条件绑定的参数（会被查询表单重置和改变的参数）
  queryParam?: Record<string, string | number | undefined | never>
  // 日期区间选择配置
  dateRangeQuery?: Record<string, [string, string]>
  // 高级搜索 展开/关闭
  advanced?: boolean
  // 列表数据
  list?: T[]
  // 是否将children转化为_children
  childrenConvert?: boolean
  // 是否从mixin中自动获取初始的列表数据
  getListFromMixin?: boolean
  // 标记加载状态
  loading?: boolean
  // 标记导出
  exportLoadingData?: boolean
  // 是否允许撤回删除状态
  allowCancelDeleted?: boolean
  // 是否重新加载
  reload?: boolean
  // 当前激活value
  currentPrimaryValue?: string | number
  // 分页数据
  pagination?: Pagination
}

export class BaseListPageLoader<T> {
  public options: ListOptions<T> = reactive({
    // 主键字段名
    primaryKey: 'id',
    // 请求接口基础路径
    baseApi: '/',
    // 列表数据接口
    listApi: '',
    // 删除接口
    deleteApiPrefix: '',
    // 导出接口
    exportApi: '',
    // 自定义参数（不被查询表单重置和改变的参数）
    customQueryParam: {},
    // 与查询条件绑定的参数（会被查询表单重置和改变的参数）
    queryParam: {},
    // 日期区间选择配置
    dateRangeQuery: {},
    // 高级搜索 展开/关闭
    advanced: false,
    // 列表数据
    list: [],
    // 是否将children转化为_children
    childrenConvert: true,
    // 是否从mixin中自动获取初始的列表数据
    getListFromMixin: true,
    // 标记加载状态
    loading: false,
    // 标记导出
    exportLoadingData: false,
    // 是否允许撤回删除
    allowCanceledDelete: false,
    // 是否重新加载
    reload: false,
    // 当前激活value
    currentPrimaryValue: '',
    // 分页数据
    pagination: {
      pageSize: 10,
      current: 1,
      total: 0,
      showSizeChanger: true,
      pageSizeOptions: ['10', '20', '30', '50', '100']
    }
  })
  /**
   * 搜索，查询第一页
   */
  public onSearch() {
    if (this.options?.pagination) this.options.pagination.current = 1
    this.getList()
  }

  /**
   * 防抖搜索（防止重复触发）
   */
  public onDebounceSearch = _.debounce(() => {
    this.onSearch()
  }, 300)

  public toggleLoading() {
    if (this.options) this.options.loading = true
  }
  /**
   * get请求获取列表
   * @returns {Promise<any>}
   */
  public async getList() {
    try {
      this.toggleLoading()
      const res = await api.get<T[]>(
        this.options?.listApi ? this.options?.listApi : `${this.options?.baseApi}/list`,
        this.buildQueryParam()
      )
      if (res.code === 0) {
        if (this.options) this.options.list = this.listFilter(res.data)
        if (res.page) {
          if (this.options?.pagination) {
            this.options.pagination.pageSize = res.page.pageSize
            this.options.pagination.current = res.page.pageIndex
            this.options.pagination.total = res.page.totalCount ? Number(res.page.totalCount) : 0
          }
        }
      } else {
        ElNotification.error({
          title: '获取列表数据失败',
          message: res.msg
        })
      }
    } catch (e) {
      ElNotification.error({
        title: '获取列表数据失败',
        message: e as string
      })
    } finally {
      this.toggleLoading()
      console.log(this.options?.loading)
    }
  }
  /**
   * 列表过滤器
   * @param list
   * @returns {*}
   */
  public listFilter(list: T[] | undefined) {
    if (!list || list.length === 0) {
      return []
    }
    return list
  }

  public paginationChanged() {
    if (this.options?.queryParam) {
      this.options.queryParam.pageIndex = this.options.pagination?.current || 1
      this.options.queryParam.pageSize = this.options.pagination?.pageSize || 20
      this.getList()
    }
  }

  /**
   * 构建排序
   * @param column
   * @param prop
   * @param order
   */
  public orderChanged(prop: string, order: string) {
    if (this.options?.queryParam) {
      if (prop !== undefined && order !== undefined) {
        this.options.queryParam.orderBy = `${prop}:${order === 'ascending' ? 'ASC' : 'DESC'}`
      } else {
        this.options.queryParam.orderBy = undefined
      }
    }
  }

  /**
   * 构建查询参数
   */
  public buildQueryParam() {
    this.dateRange2queryParam()
    // 进行前置处理，获取查询条件初始值
    let tempQueryParam = this.beforeBuildQueryParam()
    // 合并自定义查询参数
    _.merge(tempQueryParam, this.options?.customQueryParam)
    // 合并搜索参数
    _.merge(tempQueryParam, this.options?.queryParam)
    // 进行后置处理，可用于改造查询条件（用于列表页扩展等场景）
    tempQueryParam = this.afterBuildQueryParam(tempQueryParam)
    return tempQueryParam
  }
  /**
   * 查询条件前置处理
   */
  beforeBuildQueryParam(): object {
    return {}
  }
  /**
   * 查询条件后置处理
   */
  afterBuildQueryParam(queryParam: object): object {
    return queryParam
  }

  clearQueryParam(): void {
    this.options.queryParam = {}
  }
  public onReset(): void {
    this.clearQueryParam()
    this.getList()
  }
  /**
   * 构建区间查询参数
   */
  dateRange2queryParam() {
    const dateRangeQuery = this.options?.dateRangeQuery || {}
    for (const [key, value] of Object.entries(dateRangeQuery)) {
      if (this.options != null && this.options.queryParam != null) {
        this.options.queryParam[`${key}Begin`] = value[0]
        this.options.queryParam[`${key}End`] = value[1]
      }
    }
  }

  /**
   * 批量删除数据
   *
   * @param ids
   */
  public batchRemove(ids: Array<string>): Promise<string> {
    return new Promise((resolve, reject) => {
      if (!(ids && ids.length)) {
        ElMessage.warning('未选择数据')
        reject('未选择数据')
        return
      }
      const { baseApi, batchDeleteApi } = this.options
      const url = batchDeleteApi ? batchDeleteApi : `${baseApi}/batchDelete`
      ElMessageBox.confirm('确认删除已选数据吗？', '批量删除', { type: 'warning' })
        .then(() => {
          api
            .post(url, ids)
            .then(res => {
              this.removeSuccessHandler(ids)
              resolve(res.msg)
            })
            .catch(err => {
              const msg = err.msg || err.message || '删除失败'
              ElMessage.error(msg)
              reject(msg)
            })
        })
        .catch(() => {
          reject('删除取消')
        })
    })
  }
  public remove(id: string): Promise<string> {
    return new Promise((resolve, reject) => {
      ElMessageBox.confirm('确定删除该数据吗？', '删除', { type: 'warning' })
        .then(() => {
          const { baseApi, deleteApiPrefix } = this.options
          const url = deleteApiPrefix ? `${deleteApiPrefix}/${id}` : `${baseApi}/${id}`
          api
            .delete(url)
            .then(res => {
              this.removeSuccessHandler([id])
              resolve(res.msg)
            })
            .catch(err => {
              const msg = err.msg || err.message || '删除失败'
              ElMessage.error(msg)
              reject(msg)
            })
        })
        .catch(() => {
          reject('删除取消')
        })
    })
  }
  /**
   * 删除成功处理
   *
   * @param ids
   */
  removeSuccessHandler(ids: Array<string>) {
    this.afterRemoveSuccess(ids)
    if (this.options.allowCancelDeleted === false) {
      ElMessage.success('数据删除成功')
      return
    }
    const cancelDeletedApi = this.options.cancelDeletedApi
      ? this.options.cancelDeletedApi
      : `${this.options.baseApi}/cancelDeleted`
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
                .patch(cancelDeletedApi, ids)
                .then(() => {
                  ElMessage.success('撤回成功')
                  this.afterCancelDeleted(ids)
                })
                .catch(() => ElMessage.error('撤回失败'))
            }
          },
          { default: () => '撤回' }
        )
      ])
    })
  }

  /**
   * 删除成功后置处理
   * @param ids
   */
  afterRemoveSuccess(ids: Array<string>) {
    console.log('afterRemoveSuccess', ids)
    this.onSearch()
  }

  /**
   * 取消删除后置处理
   * @param ids
   */
  afterCancelDeleted(ids: Array<string>) {
    console.log('afterRemoveSuccess', ids)
    this.onSearch()
  }
}

export default function <T>(options: HookOptions<T>) {
  let { pageLoader, autoLoad } = options
  const { baseApi, options: loaderOptions } = options
  pageLoader = pageLoader || new BaseListPageLoader<T>()
  // 赋值所有选项参数
  if (loaderOptions != null) {
    for (const [key, value] of Object.entries(loaderOptions)) {
      pageLoader.options[key as keyof ListOptions<unknown>] = value
    }
  }
  // 赋值自定义参数
  if (baseApi != null) {
    pageLoader.options.baseApi = baseApi
  }
  autoLoad = autoLoad == null ? true : autoLoad
  onMounted(() => {
    if (autoLoad) {
      pageLoader?.onSearch()
    }
  })
  const { list: dataList, queryParam, advanced, dateRangeQuery, loading, pagination } = toRefs(pageLoader.options)
  return {
    pageLoader,
    queryParam,
    dateRangeQuery,
    dataList,
    loading,
    advanced,
    pagination
  }
}
