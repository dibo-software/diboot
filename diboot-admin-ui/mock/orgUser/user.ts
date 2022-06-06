import { MockMethod } from 'vite-plugin-mock'
import { JsonResult } from '../_util'
import { mock, Random } from 'mockjs'
import { UserModel } from '../../src/views/orgUser/user/type.ts'
import crudTemplate from '../_crudTemplate'

const baseUrl = '/api/user'

const arrList = [
  ['1', '1', '松松', '123', '', '123@dibo.ltd', 'F', '女', 'A', '正常', '2022-06-01'],
  ['2', '1', '张三', '124', '18700003333', '124@dibo.ltd', 'M', '男', 'A', '正常', '2022-06-01'],
  ['3', '1', '李四', '125', '18700003334', '125@dibo.ltd', 'M', '男', 'A', '正常', '2022-06-01'],
  ['4', '2', '王五', '126', '18700003335', '126@dibo.ltd', 'F', '女', 'A', '正常', '2022-06-01'],
  ['5', '1', '何莉', '127', '', '127@dibo.ltd', 'F', '女', 'A', '正常', '2022-06-01'],
  ['6', '1', '蒋灿灿', '128', '', '128@dibo.ltd', 'M', '男', 'A', '正常', '2022-06-01']
]

const dataList = initUserList(arrList)

const crud = crudTemplate({
  baseApi: '/user',
  dataList,
  fuzzyMatchKeys: ['realname', 'userNum', 'mobilePhone', 'email']
})

export default [...Object.values(crud.api)]

function initUserList(arrList): UserModel[] {
  if (!arrList || arrList.lenght === 0) {
    return []
  }
  return arrList
    .map(arr => {
      if (arr.length < 11) {
        return undefined
      }
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
      }
    })
    .filter(item => item !== undefined)
}
