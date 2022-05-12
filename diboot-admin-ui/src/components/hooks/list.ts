import { ElNotification } from 'element-plus'
import { Ref } from 'vue'

export interface BaseModel {
  id: string
}

import { reactive } from 'vue'

type HookOptions<T> = {
  pageLoader?: BaseListPageLoader<T>
  baseApi: string
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
  // 导出接口
  exportApi?: string
  // 自定义参数（不被查询表单重置和改变的参数）
  customQueryParam?: { [key: string]: any }
  // 与查询条件绑定的参数（会被查询表单重置和改变的参数）
  queryParam?: { [key: string]: any }
  // 日期区间选择配置
  dateRangeQuery?: any[]
  // 高级搜索 展开/关闭
  advanced?: boolean
  // 列表数据
  list?: T[]
  // 是否将children转化为_children
  childrenConvert: boolean
  // 是否从mixin中自动获取初始的列表数据
  getListFromMixin?: boolean
  // 标记加载状态
  loading?: boolean
  // 标记导出
  exportLoadingData?: boolean
  // 是否允许撤回删除
  allowCanceledDelete?: boolean
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
    dateRangeQuery: [],
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
        console.log('this.options.list', this.options.list)
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
      // this.toggleLoadingData()
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
    return {}
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
    _.forEach(this.options?.dateRangeQuery || [], (v, k) => {
      if (k && v && v.length === 2) {
        if (this.options?.queryParam) {
          this.options.queryParam[`${k}Begin`] = v[0]
          this.options.queryParam[`${k}End`] = v[1]
        }
      }
    })
  }
}

type Results<T> = {
  pageLoader: BaseListPageLoader<T>
  queryParam: Ref<{ [p: string]: any } | undefined> | undefined
  dataList: Ref<T[] | undefined> | undefined
  loading: Ref<boolean | undefined> | undefined
  pagination: Ref<Pagination | undefined> | undefined
}

export default function <T>(options: HookOptions<T>): Results<T> {
  let { pageLoader, autoLoad } = options
  const { baseApi } = options
  pageLoader = pageLoader || new BaseListPageLoader<T>()
  pageLoader.options.baseApi = baseApi
  autoLoad = autoLoad == null ? true : autoLoad
  onMounted(() => {
    if (autoLoad) {
      pageLoader?.onSearch()
    }
  })
  const { list: dataList, queryParam, loading, pagination } = toRefs(pageLoader.options)
  return {
    pageLoader,
    queryParam,
    dataList,
    loading,
    pagination
  }
}
