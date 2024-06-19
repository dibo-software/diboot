const zhCN = {
  loginTrace: {
    userTypeId: '用户标识',
    authAccount: '用户名',
    ipAddress: '登录IP',
    authType: '登录方式',
    success: '登录状态',
    successStatus: {
      yes: '成功',
      no: '失败'
    },
    createTime: '登录时间',
    logoutTime: '退出时间',
    placeholder: {
      start: '开始时间',
      end: '结束时间',
      successStatus: '请选择登录状态'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN
