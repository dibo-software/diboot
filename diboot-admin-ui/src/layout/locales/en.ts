import type { Locale } from './zhCN'

const en: Locale = {
  layout: {
    header: {
      dark: 'Dark mode',
      light: 'Light mode',
      chatAi: 'AI Chat',
      large: 'Large',
      default: 'Medium',
      small: 'Small',
      personal: 'Personal Center',
      logout: 'Logout',
      menuSearch: 'Menu Search'
    },
    messageBell: {
      unRead: 'Unread Messages',
      all: 'All Messages',
      allMarkRead: 'Mark All as Read',
      noMessagesYet: 'No Messages Yet',
      from: 'From',
      fetchListError: 'Error Fetching Message List'
    },
    tabs: {
      closeLeft: 'Close Left Tab',
      closeRight: 'Close Right Tab',
      closeOther: 'Close Other Tabs',
      fullScreen: 'Current Page Full Screen',
      closeAll: 'Close All Tabs',
      openFullScreen: 'Open Full Screen',
      closeFullScreen: 'Exit Full Screen'
    },
    setting: {
      title: 'Layout Real-time Demo',
      alertTitle: 'The following configurations can be previewed in real-time',
      alertDescription: 'Developers can modify default values in `src/store/app.ts`.',
      layout: 'Layout Switcher',
      layoutOptions: {
        default: 'Split Column',
        dock: 'Continuous Bar',
        menu: 'Classic',
        topNav: 'Top Navigation'
      },
      enableTabs: 'Enable Tabs',
      colorPrimary: 'Primary Color',
      enableWatermark: 'Enable Watermark',
      copyConfig: 'Copy Configuration',
      reset: 'Reset to Default'
    }
  }
}

export default en
