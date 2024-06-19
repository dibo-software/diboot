import type { RelatedData } from '@/hooks/use-option'
import type { FieldRule } from 'vant'

// tree 配置
export interface TreeConfig extends RelatedData {
  parent: string
  // 指定排序接口 （需同时也应指定 ext为排序属性）
  sortApi?: string
}

// list 配置
export interface ListConfig {
  // (动态业务模型可空，反之必需)
  baseApi?: string
  // 主键属性名（默认值：id）
  primaryKey?: string
  // 右列表关联左树属性名
  relatedKey?: string
  // FormItem 其中 'span' | 'rule' | 'required' 失效
  searchArea?: FormConfig
  columns: TableColumn[]
  operations?: Operation[]
  // 行编辑
  rowEdit?: boolean
  rowForm?: FormConfig
}

// 列表操作配置
export interface Operation {
  // 按钮名称
  title: string
  // 编码
  code: string
  // 图标
  icon?: string
  // 触发类型
  triggerType: 'row' | 'more' | 'ordinary' | 'select' | 'header' | 'bottom'
  // 类型
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  // 按钮属性
  keys?: ('plain' | 'round' | 'circle' | 'text' | 'bg')[]
  // 组件类型
  componentType?: 'form' | 'custom'
  // 接口权限编码
  permissionCode?: string
  // 组件
  component?: string
  // 分体（下拉按钮组）
  split?: boolean
  // 是否预制按钮允许位置
  _prefab?: string[]
}

// 表列
export interface TableColumn {
  hide?: boolean
  // 显示列属性
  prop: string
  // 存储列属性
  column?: string
  label: string
  // 字段类型（展现形式，避免冗余代码生成）
  type?: 'multiple' | 'dict' | 'boolean'
  // 小数位数 （默认：2）
  number?: number
  width?: string | number
  sortable?: false | 'custom'
  fixed?: boolean
  filters?: Array<{ text: string; value: unknown }>
  showOverflowTooltip?: boolean
}

// 表单页配置
export interface FormConfig {
  labelWidth?: string // 默认 80px
  column?: number
  propList: FormItem[]
  relateds?: ModelRelated[]
  // 表头过滤（用于）
  tableHeader?: boolean
}

/**
 * 模型关联信息
 */
export interface ModelRelated {
  uid: string
  label: string
  appModule: string
  valueFromField: string
  modelKey: string
  modelLabel: string
  fieldKey: string
  relType: string
  desc: string
  // 反向（该模型关联了当前）
  reverse: boolean
}

// 详情页配置
export interface DetailConfig {
  labelWidth?: string
  column?: number // 默认3
  propList: DetailProp[]
  // 标题（存在关联的详情展示时显示）
  title?: string
  // 关联
  relatedMode?: 'tabs'
  relateds?: ModelRelated[]
}

export interface DetailProp {
  span?: number // 默认 1
  prop: string
  label: string
  files?: string // VO中关联文件记录列表属性名
  image?: boolean // 关联文件是否为图片
  type?: 'textarea' | 'rich' /* 文本域、富文本 */ | 'multiple' | 'dict' | 'dict-multiple' | 'boolean' //（展现形式，避免冗余代码生成）
}

// 选项数据过滤控制
export interface Control {
  // 加载器条件属性值来源（当前模型）属性名
  prop: string
  // 加载器条件属性（当前属性的loader.type模型属性名）
  condition: string
}

// 输入类型
export interface Input {
  span?: number
  prop: string
  label?: string
  placeholder?: string
  disabled?: boolean

  rules?: FieldRule[]
  required?: boolean
  unique?: boolean
  defaultVal?: unknown
}

export interface InputText extends Input {
  type: 'input'
  maxlength?: number
}

export interface Textarea extends Omit<InputText, 'type'> {
  type: 'textarea'
  autosize?: boolean | { minRows?: number; maxRows?: number }
}

export interface RichText extends Input {
  type: 'rich'
  height?: string
  // 编辑器模式
  mode?: 'default' | 'simple'
}

export interface InputNumber extends Input {
  type: 'input-number' | 'input-number-range' // 数字区间用于列表过滤
  min?: number
  max?: number
  // 精度（未指定则为整数）
  precision?: number
  controls?: false | 'right'
}

export interface BooleanSwitch extends Input {
  type: 'boolean'

  // 默认 select
  mode?: 'select' | 'switch'
}

export interface Checkbox extends Input {
  type: 'checkbox'
  loader?: string
  labelProp: string
}

export interface Radio extends Omit<Checkbox, 'type'> {
  type: 'radio'
}

export interface Select extends Input {
  type: 'select'
  loader: string | RelatedData
  labelProp: string
  // 选项动态过滤（附加条件）
  control?: Control
  remote?: boolean
  multiple?: boolean
}

export interface TreeSelect extends Omit<Select, 'type' | 'remote'> {
  type: 'tree-select' | 'cascader'
  loader: RelatedData
  lazy?: boolean
  checkStrictly?: boolean
}

export interface DateTime extends Input {
  type: 'year' | 'month' | 'date' | 'datetime' | 'week' | 'daterange' | 'datetimerange'
  format?: string
}

export interface Time extends Input {
  type: 'time'
}

export interface Upload extends Input {
  type: 'upload'
  listType?: 'text' | 'picture' | 'picture-card'
  limit?: number
  accept?: string
  // 文件大小限制（单位：兆 MB）
  size?: number
  // VO中关联文件记录列表属性名
  files?: string
}

export interface ListSelector extends Input {
  type: 'list-selector'
  tree?: Omit<TreeConfig, 'sortApi'>
  // list配置 (关联对象为动态业务模型可空，反之必需)
  list?: Omit<ListConfig, 'operation'>
  // dataType 所属模块
  appModule: string
  // list 中的对象类型（用于回显值获取）
  dataType: string
  // 列表数据显示字段；默认取list第一列（list.columns[0].prop）
  dataLabel?: string
  multiple?: boolean
  // tree根节点
  rootId?: string
}

export type FormItem =
  | InputText
  | Textarea
  | RichText
  | InputNumber
  | BooleanSwitch
  | Checkbox
  | Radio
  | Select
  | TreeSelect
  | DateTime
  | Time
  | Upload
  | ListSelector

/**
 * 页面配置
 */
interface PageConfig {
  id: string
  /**
   * 标题
   */
  title: string
  /**
   * 实现方式：动态，静态
   */
  implWay: 'DYNAMIC' | 'STATIC'
  /**
   * 动态模式
   */
  dynamicMode: 'CONFIG' | 'DESIGN'
  /**
   * 静态组件路径
   */
  staticPath?: string
  /**
   * 打开模式
   */
  openMode: 'DIALOG' | 'NEW_TAB'
  /**
   * 关联模型id
   */
  modelId: string
  appModule?: string
  modelKey: string
  modelLabel: string
  /**
   * 编辑状态
   */
  status: string
  /**
   * 上次发布快照
   */
  snapshots?: string
}
/**
 * pageType: 页面类型 'LIST' | 'FORM' | 'DETAIL'
 * configData： 配置数据
 */
interface ListPage extends PageConfig {
  pageType: 'LIST'
  configData: { tree?: TreeConfig; list: ListConfig }
}
interface FormPage extends PageConfig {
  pageType: 'FORM'
  configData: FormConfig
}
interface DetailPage extends PageConfig {
  pageType: 'DETAIL'
  configData: DetailConfig
}

/**
 * 页面设计
 */
export type PageDesign = ListPage | FormPage | DetailPage

export type SelectedValue = { selectedValues: string[]; selectedOptions: LabelValue[] }

export type SelectedRowValue = { selectedValues: string[]; selectedRows: Record<string, any>[] }
