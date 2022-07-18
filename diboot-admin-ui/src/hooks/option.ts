import type { ApiData } from '@/utils/request'

/**
 * 绑定对象
 */
export interface RelatedData {
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
  next?: RelatedData
  // 附加条件
  condition?: Record<string, boolean | string | number | (string | number)[] | null>
}

/**
 * 异步绑定对象
 */
export interface AsyncRelatedData extends RelatedData {
  // 远程过滤的关键字
  keyword?: string
  // 禁止加载数据（用于联动的远程过滤）
  disabled?: boolean
}

/**
 * 联动控制器
 */
export interface LinkageControl {
  // 受控的属性名
  prop: string
  // 选项加载器（指向 asyncBind 的 key）
  loader: string
  // 加载器条件属性
  condition: string
  // 自动加载数据（默认：true）
  autoLoad?: boolean
}

/**
 * RelatedData 配置选项
 */
export interface RelatedDataOption {
  // 字典类型 （RelatedData 中的字典类型数据 key 为：[字典类型小驼峰Options]）
  dict?: string | string[]
  // 绑定对象（key 将作为从 RelatedData 中获取数据的 key）
  load?: Record<string, RelatedData>
  // 异步绑定对象（key 同 bind，value 将作为异步获取数据的 loader）
  asyncLoad?: Record<string, AsyncRelatedData>
  // 联动控制器（依赖 asyncBind 加载 RelatedData 中数据）
  linkageControl?: Record<string, LinkageControl | LinkageControl[]>
}

/**
 * 选项数据源加载
 */
export default (option: RelatedDataOption) => {
  const { dict, load, asyncLoad, linkageControl } = option

  // 数据集合
  const relatedData: Record<string, LabelValue[]> = reactive({})

  /**
   * 初始化 RelatedData
   */
  const initRelatedData = async () => {
    const reqList: Promise<ApiData<Record<string, LabelValue[]>>>[] = []
    // 通用获取关联字典的数据
    if ((dict ?? []).length > 0)
      reqList.push(api.post('/common/loadRelatedDict', dict instanceof Array ? dict : [dict]))
    // 通用获取关联绑定的数据
    if (Object.keys(load ?? []).length > 0) reqList.push(api.post('/common/loadRelatedData', load))

    if (reqList.length > 0) {
      const resList = await Promise.all(reqList)
      resList.forEach(res => {
        if (res.code === 0) Object.assign(relatedData, res.data)
        else ElNotification.error({ title: '获取选项数据失败', message: res.msg })
      })
    }
  }

  // 异步加载状态
  const asyncLoading = ref(false)

  /**
   * 加载 RelatedData
   *
   * @param relatedDataLoader 加载器
   * @param nodeData 节点数据（可空）
   */
  const loadRelatedData = async (relatedDataLoader: AsyncRelatedData, nodeData?: LabelValue) => {
    if (relatedDataLoader.disabled) {
      return []
    }
    asyncLoading.value = true
    const build = (item?: string) => (item ? `/${item}` : '')
    const res = await api.get<LabelValue[]>(
      `/common/loadRelatedData${build(nodeData?.value)}${build(nodeData?.type)}`,
      relatedDataLoader
    )
    asyncLoading.value = false
    if (res.code === 0) return res.data ?? []
    else ElNotification.error({ title: '获取选项数据失败', message: res.msg })
    return []
  }

  /**
   * 获取异步绑定加载器
   *
   * @param loader 加载器key
   */
  const findAsyncLoader = (loader: string): AsyncRelatedData => {
    if (asyncLoad == null) {
      throw new Error(`No async bind! Please check 'asyncBind'!`)
    }
    const relatedDataLoader = asyncLoad[loader]
    if (relatedDataLoader == null) {
      throw new Error(`Please check 'asyncBind', '${loader}' that does not exist!`)
    }
    return relatedDataLoader
  }

  /**
   * 远程过滤加载选项
   *
   * @param value 输入值
   * @param loader 加载器（asyncBind 的 key）
   */
  const remoteRelatedDataFilter = async (value: string, loader: string) => {
    if (value == null || (value = value.trim()).length === 0) {
      relatedData[loader] = []
      return
    }
    const relatedDataLoader = findAsyncLoader(loader)
    relatedDataLoader.keyword = value
    relatedData[loader] = await loadRelatedData(relatedDataLoader)
  }

  /**
   * 异步加载tree数据
   *
   * @param nodeData 当前tree节点数据
   * @param loader 加载器名称
   * @param resolve
   */
  const lazyLoadRelatedData = async (
    nodeData: LabelValue,
    loader: string,
    resolve: (options: LabelValue[]) => void
  ) => {
    const relatedDataLoader = findAsyncLoader(loader)
    const dataLsit = (await loadRelatedData(relatedDataLoader, nodeData)) ?? []
    if (dataLsit.length === 0 && relatedDataLoader.next != null) nodeData.disabled = true
    resolve(dataLsit)
  }

  /**
   * 处理联动
   *
   * @param value 选项值
   * @param controlKey
   * @param form 表单（当需要重置被控值时）
   */
  const handleLinkage = (value: string, controlKey: string, form: Record<string, unknown>) => {
    if (linkageControl == null) {
      throw new Error(`No control! Please check 'linkageControl'!`)
    }
    const controlItem = linkageControl[controlKey]
    if (controlItem == null) {
      throw new Error(`Please check 'linkageControl', '${controlKey}' that does not exist!`)
    }
    const isNull = value == null || value.length === 0
    const execute = async ({ prop, loader, condition, autoLoad }: LinkageControl) => {
      const relatedDataLoader = findAsyncLoader(loader)
      relatedDataLoader.disabled = isNull
      if (relatedDataLoader.condition == null) relatedDataLoader.condition = {}
      relatedDataLoader.condition[condition] = value
      if (form) form[prop] = undefined
      relatedData[loader] = autoLoad === false || isNull ? [] : await loadRelatedData(relatedDataLoader)
    }
    controlItem instanceof Array ? controlItem.forEach(item => execute(item)) : execute(controlItem)
  }

  return {
    relatedData,
    initRelatedData,
    asyncLoading,
    remoteRelatedDataFilter,
    lazyLoadRelatedData,
    handleLinkage
  }
}
