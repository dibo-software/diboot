import type { FormInstance } from 'element-plus'
import type { ApiData } from '@/utils/request'

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
      this.options.title = '数据新建'
      this.afterOpen(id)
      return
    }
    try {
      this.options.visible = true
      this.options.loading = true
      this.options.title = '数据更新'
      await this.getFormModel(id)
      await this.afterOpen(id)
    } catch (e) {
      console.log('获取详情数据失败', e)
    } finally {
      this.options.loading = false
    }
  }

  public async afterOpen(id?: string) {
    console.log('afterOpen id', id)
  }

  public async getFormModel(id: string) {
    const res = await api.get<T>(this.options?.getApi ? this.options?.getApi : `${this.options?.baseApi}/${id}`)
    if (res.code === 0) {
      this.options.model = res.data
    }
  }

  public async onSubmit(formEl: FormInstance | undefined) {
    this.options.confirmSubmit = true
    try {
      await this.validate(formEl)
      if (!this.options?.model) {
        throw new Error('表单数据异常')
      }
      const values = await this.enhance(this.options.model)
      const id: unknown = this.options.model[this.options.primaryKey as keyof T]
      let res
      if (id) {
        res = await this.update(id, values)
      } else {
        res = await this.create(values)
      }
      this.afterSubmitSuccess(res)
    } catch (e) {
      console.log('e', e)
    } finally {
      this.options.confirmSubmit = false
    }
  }

  public async create(values: T): Promise<ApiData<T>> {
    const { baseApi, createApi } = this.options
    const url = createApi ? createApi : `${baseApi}/`
    return await api.post(url, values)
  }

  public async update(id: unknown, values: T): Promise<ApiData<T>> {
    const { baseApi, updateApiPrefix } = this.options
    const url = updateApiPrefix ? `${updateApiPrefix}/${id}` : `${baseApi}/${id}`
    return await api.put(url, values)
  }

  /**
   * 提交成功后置处理
   * @param res
   */
  public afterSubmitSuccess(res: ApiData<T>) {
    this.options.visible = false
    console.log('afterSubmitSuccess', res)
  }

  public validate(formEl: FormInstance | undefined) {
    return new Promise((resolve, reject) => {
      if (!formEl) {
        reject('表单实例异常')
        return
      }
      formEl.validate((valid, fields) => {
        if (valid) {
          resolve(fields)
        } else {
          reject('校验失败')
        }
      })
    })
  }

  /**
   * 对表单数据进行提交前对加强
   * @param values
   */
  public async enhance(values: T): Promise<T> {
    return values
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
    // _.merge(pageLoader.options, loaderOptions)
  }
  // 赋值自定义参数
  if (baseApi != null) {
    pageLoader.options.baseApi = baseApi
  }
  const { model, title, loading, confirmSubmit } = toRefs(pageLoader.options)

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
    loading,
    confirmSubmit
  }
}
