import type { ApiData } from '@/utils/request'
import qs from 'qs'

/**
 * 绑定对象
 */
export interface RelatedData {
  // 应用模块
  appModule?: string
  // 对象类型（类名）
  type: string
  // 显示属性
  label: string
  // 扩展数据
  ext?: string
  // 排序
  orderBy?: string

  // 父级ID存储属性（用于Tree结构数据；如：parentId）
  parent?: string
  // 父级ID路径存储属性（用于Tree结构数据远程过滤向上查找父节点；如：parentIdsPath）
  parentPath?: string
  // 懒加载（默认：true ；为 false 时会同步加载下一级，且当为树时会加载整个树）
  lazyChild?: boolean
  // 附加条件
  conditions?: Array<ConditionItem>
  /**
   * 附加条件
   * @Deprecated 3.3移除，使用 conditions?: Array<ConditionItem> 代替
   */
  condition?: Record<string, boolean | string | number | (string | number)[] | null | undefined>
}

/**
 * 条件项
 */
export interface ConditionItem {
  field: string
  comparison?: string // def EQ =
  value?: unknown
}

/**
 * 异步绑定对象
 */
export interface AsyncRelatedData extends RelatedData {
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
  // 默认模块
  defModule?: string
  // 请求接口基础路径（默认：/common）
  baseApi?: string
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
export default ({
  defModule = '',
  baseApi = inject<string>('related-data-base-api', '/common'),
  dict,
  load,
  asyncLoad,
  linkageControl
}: RelatedDataOption) => {
  // 数据集合
  const relatedData: Record<string, LabelValue[]> = reactive({})

  // 初始化加载状态
  const initLoading = ref(false)

  const buildUriPrefix = (val?: string) => (val ? `/${val}` : '')

  /**
   * 初始化 RelatedData
   */
  const initRelatedData = () => {
    console.log('after initRelatedData optionsProps.value load', load)
    const reqList: Promise<ApiData<Record<string, LabelValue[]>>>[] = []
    // 通用获取关联字典的数据
    if ((dict ?? []).length > 0)
      reqList.push(
        api.post(`${buildUriPrefix(defModule)}/common/load-related-dict`, Array.isArray(dict) ? dict : [dict])
      )
    // 通用获取关联绑定的数据
    if (load && Object.keys(load).length > 0) {
      const loadMap: Record<string, Record<string, RelatedData>> = {}
      for (const key in load) {
        const module = load[key].appModule ?? defModule
        ;(loadMap[module] ? loadMap[module] : (loadMap[module] = {}))[key] = load[key]
      }
      for (const key in loadMap) {
        reqList.push(api.post(`${buildUriPrefix(key)}${baseApi}/batch-load-related-data`, loadMap[key]))
      }
    }

    return new Promise<void>((resolve, reject) => {
      if (reqList.length > 0) {
        initLoading.value = true
        Promise.all(reqList)
          .then(resList => {
            resList.forEach(res => Object.assign(relatedData, res.data))
            resolve()
          })
          .catch(err => {
            showNotify({
              type: 'danger',
              message: err?.msg || err?.message || (err?.length ? err : '初始化选项数据失败')
            })
            reject(err)
          })
          .finally(() => (initLoading.value = false))
      } else resolve()
    })
  }

  // 异步加载状态
  const asyncLoading = ref(false)

  /**
   * 加载 RelatedData
   *
   * @param relatedDataLoader 加载器
   * @param parentId 父节点ID
   * @param keyword 搜索关键字
   */
  const loadRelatedData = (relatedDataLoader: AsyncRelatedData, parentId?: string, keyword?: string) => {
    const empty = [] as LabelValue[]
    if (relatedDataLoader.disabled) return Promise.reject<LabelValue[]>(empty)
    asyncLoading.value = true
    return new Promise<LabelValue[]>(resolve => {
      const appModule = relatedDataLoader.appModule ?? defModule
      api
        .post<LabelValue[]>(
          `${buildUriPrefix(appModule)}${baseApi}/load-related-data${buildUriPrefix(parentId)}${
            keyword ? '?' + qs.stringify({ keyword }) : ''
          }`,
          relatedDataLoader
        )
        .then(res => resolve(res.data))
        .catch(err => {
          showNotify({ type: 'danger', message: err?.msg || err?.message || (err?.length ? err : '获取选项数据失败') })
          resolve(empty)
        })
        .finally(() => (asyncLoading.value = false))
    })
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
   * @param loader 加载器（asyncBind 的 key）
   * @param value 输入值
   */
  const remoteRelatedDataFilter = async (loader: string, value?: string) => {
    if (value == null || (value = value.trim()).length === 0) {
      relatedData[loader] = []
      return
    }
    relatedData[loader] = await loadRelatedData(findAsyncLoader(loader), void 0, value)
  }

  /**
   * 异步加载(tree)数据
   *
   * @param loader 加载器名称
   * @param parentId 当前tree节点数据ID，用于加载子节点列表
   */
  const lazyLoadRelatedData = async (loader: string, parentId?: string) =>
    await loadRelatedData(findAsyncLoader(loader), parentId)

  /**
   * 处理联动
   *
   * @param value 选项值
   * @param controlKey
   * @param form 表单（当需要重置被控值时）
   */
  const handleLinkage = (value: string, controlKey: string, form?: Record<string, unknown>) => {
    if (linkageControl == null) {
      throw new Error(`No control! Please check 'linkageControl'!`)
    }
    const controlItem = linkageControl[controlKey]
    if (controlItem == null) {
      throw new Error(`Please check 'linkageControl', '${controlKey}' that does not exist!`)
    }
    const isNull = value == null || value.length === 0
    const execute = async ({ prop, loader, condition, autoLoad }: LinkageControl) => {
      let relatedDataLoader = findAsyncLoader(loader)
      relatedDataLoader.disabled = isNull
      relatedDataLoader = _.cloneDeep(relatedDataLoader)
      if (relatedDataLoader.conditions == null) relatedDataLoader.conditions = []
      relatedDataLoader.conditions.push({ field: condition, value })
      if (form) form[prop] = undefined
      relatedData[loader] = autoLoad === false || isNull ? [] : await loadRelatedData(relatedDataLoader)
    }
    Array.isArray(controlItem) ? controlItem.forEach(item => execute(item)) : execute(controlItem)
  }

  return {
    relatedData,
    initLoading,
    initRelatedData,
    asyncLoading,
    loadRelatedData,
    remoteRelatedDataFilter,
    lazyLoadRelatedData,
    handleLinkage
  }
}
