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
  },
  chatAi: {
    model: 'Model',
    askMe: 'You can ask me these',
    newSession: 'New Conversation',
    createSession: 'Create New Session',
    updateSession: 'Update Session',
    deleteSession: 'Delete Session',
    title: {
      send: 'Click to Send'
    },
    placeholder: {
      problem: 'Please enter your question'
    }
  },
  exception: {
    service404: 'Page does not exist or service unavailable',
    service500: 'Service error',
    backHome: 'Return to home page'
  },
  login: {
    tenantCode: 'TenantCode',
    username: 'Username',
    password: 'Password',
    captcha: 'Captcha',
    submit: 'Login',
    cas: 'CAS SSO Login',
    oauth2: 'OAuth2 SSO Login',
    loading: 'Logging',
    success: 'Login success'
  },
  personal: {
    uploadImg: 'Please upload image file',
    modifyAvatar: 'Change Avatar',
    avatarSetting: 'Avatar Settings',
    info: 'Personal Information',
    oldPassword: 'Current Password',
    newPassword: 'New Password',
    confirmPassword: 'Confirm Password',
    passwordDifferent: 'Passwords entered twice are inconsistent',
    passwordLength: 'Password length should be greater than 8 characters',
    passwordRule: 'Contains numbers, lowercase letters, uppercase letters, and at least two types',
    updateFailed: 'Update failed',
    uploadFailed: 'Upload failed',
    rules: {
      oldPassword: 'Enter original password',
      password: 'Enter new password',
      confirmPassword: 'Please re-enter the original password'
    }
  }
}

export default en
