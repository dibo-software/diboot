import type { RelatedData } from '@/hooks/use-option'
import type { FormItemRule } from 'element-plus'

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
  // FormItem 其中 'span' | 'rule' | 'required' 失效 (colSpan 默认 8 （24/8 三列）)
  searchArea?: FormConfig
  columns: TableColumn[]
  operation?: ListOperation
  indexColumn?: boolean
}

// 列表操作
export type ListOperation = Partial<
  Record<'detail' | 'create' | 'update' | 'remove' | 'batchRemove' | 'exportData' | 'importData', boolean>
>

// 表列
export interface TableColumn {
  hide?: boolean
  prop: string
  // 存储列属性
  column?: string
  label: string
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

  rules?: FormItemRule[]
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
  loader?: string | RelatedData
  labelProp: string
  // 选项动态过滤（附加条件）
  control?: Control
  remote?: boolean
  multiple?: boolean
}

export interface TreeSelect extends Omit<Select, 'type' | 'remote'> {
  type: 'tree-select' | 'cascader'
  loader?: RelatedData
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
