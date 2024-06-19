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
  }
}

export type Locale = typeof zhCN

export default zhCN
