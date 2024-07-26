import type { Locale } from './zhCN'
const en: Locale = {
  loginTrace: {
    userTypeId: 'User Identifier',
    authAccount: 'Username',
    ipAddress: 'Login IP',
    authType: 'Authentication Method',
    success: 'Login Status',
    successStatus: {
      yes: 'Successful',
      no: 'Failed'
    },
    onlineStatusLabel: 'Online Status',
    onlineStatus: {
      online: 'Online',
      logout: 'Logged Out',
      unknown: '-',
      invalid: 'Invalid'
    },
    browserInfo: 'Browser',
    osInfo: 'OS',
    createTime: 'Login Time',
    logoutTime: 'Logout Time',
    forceLogout: 'Force Logout',
    formLogoutMessage: {
      confirmContent: 'Confirm that the user is forcibly logged out?',
      success: 'Force logout success',
      failed: 'Force logout failed'
    },
    placeholder: {
      start: 'Start Time',
      end: 'End Time',
      successStatus: 'Please select login status'
    }
  }
}

export default en
