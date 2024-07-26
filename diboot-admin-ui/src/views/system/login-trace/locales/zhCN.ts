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
    onlineStatusLabel: '在线状态',
    onlineStatus: {
      online: '在线',
      logout: '已退出',
      unknown: '-',
      invalid: '已失效'
    },
    browserInfo: '浏览器',
    osInfo: '操作系统',
    createTime: '登录时间',
    logoutTime: '退出时间',
    forceLogout: '强退',
    formLogoutMessage: {
      confirmContent: '确认强制退出该用户？',
      success: '强退成功',
      failed: '强退失败'
    },
    placeholder: {
      start: '开始时间',
      end: '结束时间',
      successStatus: '请选择登录状态'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN
