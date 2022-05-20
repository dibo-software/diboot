type HookOptions<T> = {
  pageLoader?: BaseFormLoader<T>
  options?: FormOptions<T>
  baseApi?: string
}

export interface FormOptions<T> {
  // 标题
  title?: string
  // 主键字段名
  primaryKey?: string
  // 请求接口基础路径
  baseApi?: string
  // 查询接口
  getApi?: string
  // 新建接口
  createApi?: string
  // 更新接口前缀
  updateApiPrefix?: string
  // 表单数据
  model?: T
  // 状态数据
  visible?: boolean
  loading?: boolean
  confirmSubmit?: boolean
}

export class BaseFormLoader<T> {
  public options: FormOptions<T> = reactive({
    title: '',
    primaryKey: 'id',
    baseApi: '/',
    // 查询接口
    getApi: '',
    // 新建接口
    createApi: '',
    // 更新接口前缀
    updateApiPrefix: '',
    visible: false,
    loading: false,
    confirmSubmit: false
  })

  public async open(id?: string) {
    if (id == null) {
      this.options.visible = true
      return
    }
    await this.getFormModel(id)
  }

  public async afterOpen(id: string) {
    console.log('afterOpen id', id)
  }

  public async getFormModel(id: string) {
    const res = await api.get<T>(this.options?.getApi ? this.options?.getApi : `${this.options?.baseApi}/${id}`)
    if (res.code === 0) {
      this.options.model = res.data
    }
  }
}

export default function <T>(options: HookOptions<T>) {
  let { pageLoader } = options
  const { baseApi, options: loaderOptions } = options
  pageLoader = pageLoader || new BaseFormLoader<T>()
  // 赋值所有选项参数
  if (loaderOptions != null) {
    for (const [key, value] of Object.entries(loaderOptions)) {
      pageLoader.options[key as keyof FormOptions<any>] = value
    }
  }
  // 赋值自定义参数
  if (baseApi != null) {
    pageLoader.options.baseApi = baseApi
  }
  const { model, title, visible, loading, confirmSubmit } = toRefs(pageLoader.options)
  return {
    pageLoader,
    model,
    title,
    visible,
    loading,
    confirmSubmit
  }
}
