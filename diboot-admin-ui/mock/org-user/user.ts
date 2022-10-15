import type { MockMethod } from 'vite-plugin-mock'
import { JsonResult } from '../_util'
import { mock, Random } from 'mockjs'
import type { UserModel } from '@/views/org-structure/user/type'
import crudTemplate from '../_util/crud-template'

const baseUrl = '/api/user'

const arrList: any[][] = [
  ['1', '1', '松松', '123', '', '123@dibo.ltd', 'F', '女', 'A', '正常', '2022-06-01'],
  ['2', '1', '张三', '124', '18700003333', '124@dibo.ltd', 'M', '男', 'A', '正常', '2022-06-01'],
  ['3', '1', '李四', '125', '18700003334', '125@dibo.ltd', 'M', '男', 'A', '正常', '2022-06-01'],
  ['4', '2', '王五', '126', '18700003335', '126@dibo.ltd', 'F', '女', 'A', '正常', '2022-06-01'],
  ['5', '1', '何莉', '127', '', '127@dibo.ltd', 'F', '女', 'A', '正常', '2022-06-01'],
  ['6', '1', '蒋灿灿', '128', '', '128@dibo.ltd', 'M', '男', 'A', '正常', '2022-06-01']
]

const dataList = initUserList(arrList)

const crud = crudTemplate({
  baseApi: '/iam/user',
  dataList,
  keywordsKeys: ['realname', 'userNum', 'mobilePhone', 'email'],
  fuzzyMatchKeys: ['realname', 'userNum', 'mobilePhone', 'email']
})

export default [...Object.values(crud.api)]

function initUserList(arrList: any[][]): UserModel[] {
  if (!arrList || arrList.length === 0) {
    return []
  }
  return arrList
    .filter((arr: any[]) => arr.length >= 11)
    .map(arr => {
      return {
        id: arr[0],
        orgId: arr[1],
        realname: arr[2],
        userNum: arr[3],
        mobilePhone: arr[4],
        email: arr[5],
        gender: arr[6],
        genderLabel: arr[7],
        status: arr[8],
        statusLabel: arr[9],
        createTime: arr[10]
      } as UserModel
    })
}
