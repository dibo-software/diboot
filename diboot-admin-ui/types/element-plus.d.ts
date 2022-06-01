import 'element-plus'

declare module 'element-plus' {
  // 类型扩展

  interface UploadFile {
    uuid?: string
    accessUrl?: string
  }
}

export {}
