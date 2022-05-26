import { ApiData } from '@/utils/request'

/**
 * 绑定对象
 */
export interface BindData {
  // 对象类型（类名）
  type: string
  // 绑定属性（默认对象主键）
  value?: string
  // 显示属性
  label: string
  // 扩展数据
  ext?: string | string[]
  // 排序
  orderBy?: string
  // 父级属性（tree 为 true 时，默认为：parentId）
  parent?: string
  // 是否为 Tree 结构数据（默认：false）
  tree?: boolean
  // 懒加载（默认：true ；为 false 时会同步加载下一级，且当为树时会加载整个树）
  lazyChild?: boolean
  // 下一级
  next?: BindData
  // 附加条件
  condition?: Record<string, boolean | string | number | (string | number)[] | null>
}

/**
 * 异步绑定对象
 */
export interface AsyncBindData extends BindData {
  // 远程过滤值
  keyword?: string
  // 禁止加载数据
  disabled?: boolean
}

/**
 * 绑定控制器
 */
export interface BindControl {
  // 受控的属性名
  prop: string
  // 选项加载器
  loader: string
  // 加载器条件属性
  condition: string
  // 自动获取加载器数据（默认：true）
  auto?: boolean
}

/**
 * 数据类型
 */
export interface LabelValue<E = never> {
  // 存储值
  value: string
  // 显示值
  label: string
  // 扩展值
  ext?: E
  // 对象类型
  type?: string
  // 是否为叶子节点
  leaf?: boolean
  // 是否禁用
  disabled?: boolean
  // 子节点集合
  children?: LabelValue<E>
}

/**
 * more 配置选项
 */
export interface MoreOption {
  // 自定义获取 more 接口
  getMoreApi?: string
  // 字典类型
  dict?: string | string[]
  // 绑定对象
  bind?: Record<string, BindData>
  // 异步绑定对象
  asyncBind?: Record<string, AsyncBindData>
  // 绑定控制器
  control?: Record<string, BindControl | BindControl[]>
}

export default (option: MoreOption) => {
  const asyncBindLoading = ref(false)
  const { getMoreApi, dict, bind, asyncBind, control } = option

  const more: Record<string, LabelValue[]> = reactive({})

  /**
   * 初始化 more
   */
  const initMore = async () => {
    const reqList: Promise<ApiData<Record<string, LabelValue[]>>>[] = []
    // 个性化接口
    if (getMoreApi) reqList.push(api.get(`${getMoreApi}`))
    // 通用获取关联字典的数据
    if ((dict ?? []).length > 0) reqList.push(api.post('/common/bindDict', dict instanceof Array ? dict : [dict]))
    // 通用获取关联绑定的数据
    if (Object.keys(bind ?? []).length > 0) reqList.push(api.post('/common/bindData', bind))

    if (reqList.length > 0) {
      const resList = await Promise.all(reqList)
      resList.forEach(res => {
        if (res.code !== 0) {
          ElNotification.error({
            title: '获取选项数据失败',
            message: res.msg
          })
          return
        }
        Object.assign(more, res.data)
      })
    }
  }

  /**
   * 获取异步绑定加载器
   *
   * @param loader 加载器key
   */
  const findAsyncBindLoader = (loader: string): AsyncBindData => {
    if (asyncBind == null) {
      throw new Error(`No async bind! Please check 'asyncBind'!`)
    }
    const moreLoader = asyncBind[loader]
    if (moreLoader == null) {
      throw new Error(`Please check 'asyncBind', '${loader}' that does not exist!`)
    }
    return moreLoader
  }

  /**
   * 远程过滤加载选项
   *
   * @param value 输入值
   * @param loader 加载器类型
   */
  const remoteMoreFilter = async (value: string, loader: string) => {
    if (value == null || (value = value.trim()).length === 0) {
      more[loader] = []
      return
    }
    asyncBindLoading.value = true
    const moreLoader = findAsyncBindLoader(loader)
    moreLoader.keyword = value
    more[loader] = await loadMore(moreLoader)
    asyncBindLoading.value = false
  }

  /**
   * 加载 More
   *
   * @param moreLoader 加载器
   * @param nodeData 节点数据（可空）
   */
  const loadMore = async (moreLoader: AsyncBindData, nodeData?: LabelValue) => {
    if (moreLoader.disabled) {
      return []
    }
    const res = await api.get<LabelValue[]>(
      `/common/bindData/${nodeData?.value ?? ''}/${nodeData?.type ?? ''}`,
      moreLoader
    )
    if (res.code === 0) return res.data ?? []
    else ElNotification.error({ title: '获取选项数据失败', message: res.msg })
    return []
  }

  /**
   * 异步加载更多
   *
   * @param node 当前tree节点
   * @param loader 加载器名称
   * @param resolve
   */
  const lazyLoadMore = async (node: { data: LabelValue }, loader: string, resolve: (options: LabelValue[]) => void) => {
    const moreLoader = findAsyncBindLoader(loader)
    const dataLsit = (await loadMore(moreLoader, node.data)) ?? []
    if (dataLsit.length === 0 && moreLoader.next != null) node.data.disabled = true
    resolve(dataLsit)
  }

  /**
   * 控制相关选项的获取
   *
   * @param value 选项值
   * @param controlKey
   * @param form
   */
  const controlRelationOptions = (value: string, controlKey: string, form: Partial<Record<string, unknown>>) => {
    if (control == null) {
      throw new Error(`No control! Please check 'control'!`)
    }
    const controlItem = control[controlKey]
    if (controlItem == null) {
      throw new Error(`Please check 'control', '${controlKey}' that does not exist!`)
    }
    const isNull = value == null || value.length === 0
    const execute = async ({ prop, loader, condition, auto }: BindControl) => {
      const moreLoader = findAsyncBindLoader(loader)
      moreLoader.disabled = isNull
      if (moreLoader.condition == null) moreLoader.condition = {}
      moreLoader.condition[condition] = value
      form[prop] = undefined
      if (auto !== false) more[loader] = isNull ? [] : await loadMore(moreLoader)
    }
    controlItem instanceof Array ? controlItem.forEach(item => execute(item)) : execute(controlItem)
  }

  return { asyncBindLoading, more, initMore, remoteMoreFilter, lazyLoadMore, controlRelationOptions }
}
