type HookOptions<T> = {
  pageLoader?: any
  options?: DetailOptions<T>
  baseApi?: string
}

export interface DetailOptions<T> {
  // 标题
  title?: string
  // 请求接口基础路径
  baseApi?: string
  // 查询接口
  getApi?: string
  // 详情数据
  model?: T
  // 状态数据
  visible?: boolean
  loading?: boolean
}

export class BaseDetailLoader<T> {
  public options: DetailOptions<T> = reactive({
    title: '详情数据',
    baseApi: '/',
    getApi: '',
    visible: false,
    loading: false
  })

  public async open(id: string) {
    try {
      if (!id) {
        throw new Error('参数错误')
      }
      this.options.visible = true
      this.options.loading = true
      await this.getModelData(id)
      await this.afterOpen(id)
    } catch (e) {
      console.log('详情加载失败', e)
    } finally {
      this.options.loading = false
    }
  }

  public async afterOpen(id: string) {
    console.log('afterOpen id', id)
  }

  public async getModelData(id: string) {
    const res = await api.get<T>(this.options?.getApi ? this.options?.getApi : `${this.options?.baseApi}/${id}`)
    console.log('res', res)
    if (res.code === 0) {
      this.options.model = res.data
    }
  }

  public async close() {
    this.options.visible = false
    this.options.loading = false
  }
}

export default function <T>(options: HookOptions<T>) {
  let { pageLoader } = options
  const { baseApi, options: loaderOptions } = options
  pageLoader = pageLoader || new BaseDetailLoader<T>()
  // 赋值所有选项参数
  if (loaderOptions != null) {
    for (const [key, value] of Object.entries(loaderOptions)) {
      pageLoader.options[key as keyof DetailOptions<any>] = value
    }
    // _.merge(pageLoader.options, loaderOptions)
  }
  // 赋值自定义参数
  if (baseApi != null) {
    pageLoader.options.baseApi = baseApi
  }
  const { model, title, loading } = toRefs(pageLoader.options)

  const visible = computed({
    get: () => {
      return pageLoader?.options.visible != null ? pageLoader.options.visible : false
    },
    set: (val: boolean) => {
      if (pageLoader?.options) {
        pageLoader.options.visible = val
      }
    }
  })

  return {
    pageLoader,
    model,
    title,
    visible,
    loading
  }
}
