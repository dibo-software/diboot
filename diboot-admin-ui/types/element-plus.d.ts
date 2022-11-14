import 'element-plus'
import { ElTree } from 'element-plus'

declare module 'element-plus' {
  // 类型扩展
  type ElTreeInstanceType = InstanceType<typeof ElTree>

  interface UploadFile {
    id?: string
    accessUrl?: string
  }
}

export {}
