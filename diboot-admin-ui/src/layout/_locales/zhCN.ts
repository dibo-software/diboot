const zhCN = {
  layout: {
    header: {
      dark: '深色模式',
      light: '浅色模式',
      chatAi: 'AI 对话',
      large: '大',
      default: '中',
      small: '小',
      personal: '个人中心',
      logout: '退出登录',
      menuSearch: '搜索菜单'
    },
    messageBell: {
      unRead: '未读消息',
      all: '全部消息',
      allMarkRead: '全标记已读',
      noMessagesYet: '暂无消息',
      from: '来源',
      fetchListError: '获取消息列表异常'
    },
    tabs: {
      closeLeft: '关闭左侧页签',
      closeRight: '关闭右侧页签',
      closeOther: '关闭其他页签',
      fullScreen: '当前页全屏',
      closeAll: '关闭所有页签',
      openFullScreen: '开启全屏',
      closeFullScreen: '关闭全屏'
    },
    setting: {
      title: '布局实时演示',
      alertTitle: '以下配置可实时预览',
      alertDescription: '开发者可在 `src/store/app.ts` 中修改默认值。',
      layout: '布局切换',
      layoutOptions: {
        default: '分栏',
        dock: '通栏',
        menu: '经典',
        topNav: '顶导航'
      },
      enableTabs: '开启 Tabs',
      colorPrimary: '主题色',
      enableWatermark: '开启水印',
      copyConfig: '一键复制配置',
      reset: '一键恢复默认'
    }
  },
  chatAi: {
    model: '模型',
    askMe: 'You can ask me these',
    newSession: '新对话',
    createSession: '新建对话',
    updateSession: '更新对话',
    deleteSession: '删除对话',
    title: {
      send: '点击发送'
    },
    placeholder: {
      problem: '请输入您的问题'
    }
  },
  exception: {
    service404: '页面不存在或服务不可用',
    service500: '服务异常',
    backHome: '返回首页'
  },
  login: {
    tenantCode: '租户编码',
    username: '用户名',
    password: '密 码',
    captcha: '验证码',
    submit: '登 录',
    cas: 'CAS 单点登录',
    oauth2: 'OAuth2 单点登录',
    loading: '登录中',
    success: '登录成功'
  },
  personal: {
    uploadImg: '请上传图片文件',
    modifyAvatar: '修改头像',
    avatarSetting: '头像设置',
    info: '个人信息',
    oldPassword: '当前密码',
    newPassword: '新密码',
    confirmPassword: '确认密码',
    passwordDifferent: '两次输入密码不一致',
    passwordLength: '密码长度应大于8位',
    passwordRule: '数字、小写字母、大写字母，至少包含两种',
    updateFailed: '更新失败',
    uploadFailed: '上传失败',
    rules: {
      oldPassword: '请输入原密码',
      password: '请输入新密码',
      confirmPassword: '请再次输入原密码'
    }
  }
}

export type Locale = typeof zhCN

export default zhCN
