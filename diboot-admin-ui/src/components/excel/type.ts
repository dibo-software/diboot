export interface TableHead {
  key: string
  title: string
  children?: TableHead[]
}

export interface ExcelPreview {
  id: string
  tableHeads: TableHead[]
  dataList: Record<string, unknown>[]
  totalCount: string
  errorCount?: string
  errorMsgs?: string[]
}

export interface ExcelImport {
  totalCount: string
  errorUrl?: string
  errorCount?: string
  errorMsgs?: string[]
}
