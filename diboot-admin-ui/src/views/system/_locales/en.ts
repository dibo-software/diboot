import type { Locale } from './zhCN'
const en: Locale = {
  client: {
    name: 'Client Name',
    status: 'Status',
    permissions: 'API Permissions',
    updateKey: 'Regenerate secret',
    viewLogs: 'View logs',
    namePlaceholder: 'Please enter client name',
    statusPlaceholder: 'Please select status',
    appKeyPlaceholder: 'Please enter the AppKey',
    appSecretPlaceholder: 'Automatically generated AppSecret',
    permissionsPlaceholder: 'Please select an interface permission'
  },
  config: {
    propKey: 'Property Name',
    propLabel: 'Property Label',
    propValue: 'Property Value',
    category: 'Category',
    dataType: 'Data Type',
    dataTypeOptions: {
      text: 'Text',
      textarea: 'Textarea',
      number: 'Number',
      boolean: 'Toggle'
    },
    fetchException: 'Fetching configuration information failed',
    saveException: 'Saving configuration information failed',
    ext: 'Extended Configuration'
  },
  dictionary: {
    itemName: 'Dictionary Name',
    type: 'Dictionary Code',
    description: 'Dictionary Note',
    item: {
      label: 'Dictionary Item',
      itemName: 'Item Name',
      itemValue: 'Item Code',
      color: 'Item Color',
      internationalization: 'Internationalization',
      sort: 'Sort Order',
      rules: {
        itemName: 'Please enter the item name',
        itemValue: 'Please enter the item code'
      }
    },
    rules: {
      itemName: 'Please enter the dictionary name',
      type: 'Please enter the dictionary code'
    },
    paramsError: 'Parameter Error'
  },
  fileRecord: {
    appModule: 'Business Module',
    fileName: 'File Name',
    fileType: 'File Type',
    fileSize: 'File Size',
    accessUrl: 'Access URL',
    description: 'Note',
    editDescription: 'Edit Note',
    placeholder: {
      description: 'Please enter notes'
    }
  },
  i18nConfig: {
    i18nCode: 'Resource Identifier',
    translate: 'Translate',
    type: 'Type',
    content: 'Translation Content',
    internationalTranslation: 'International Translation',
    rules: {
      i18nCode: 'Can only input letters, numbers, . , - , _'
    },
    fetchDataListError: 'Failed to retrieve list data'
  },
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
  },
  message: {
    businessType: 'Business Type',
    businessCode: 'Business Identifier',
    title: 'Title',
    content: 'Content',
    senderName: 'Sender',
    receiverName: 'Receiver',
    channel: 'Send Channel',
    result: 'Send Result',
    status: 'Message Status',
    scheduleTime: 'Scheduled Send Time',
    createTime: 'Send Date',
    placeholder: {
      title: 'Message Title',
      channel: 'Please choose the send channel',
      status: 'Please choose the message status',
      createTime: 'Send Date'
    }
  },
  messageTemplate: {
    title: 'Template Title',
    code: 'Template Code',
    appModule: 'Business Module',
    content: 'Content',
    placeholder: {
      title: "Message template name, such as: 'Reminder for Follow-up'",
      code: "Unique template code, such as: 'CBTX'",
      appModule: 'Enter a business module name',
      content: 'Send Date'
    },
    selectableVariables: 'Selectable Variables',
    addVariable: 'Add This Variable'
  },
  operationLog: {
    business: 'Business operation logs',
    client: 'Third-party application logs',
    exception: 'System exception logs',
    user: 'User',
    userRealname: 'User Name',
    userTypeAndId: 'User Type: ID',
    userType: 'User Type',
    businessObj: 'Business Object',
    operation: 'Operation Matter',
    requestMethod: 'Request Method',
    requestUri: 'Request URL',
    requestIp: 'Client IP',
    statusCode: 'Status Code',
    createTime: 'Operation Time',
    requestParams: 'Request Parameters',
    errorMsg: 'Error Message',
    placeholder: {
      requestMethod: 'Please choose the request method'
    }
  },
  resource: {
    main: 'Main menu resources',
    mobile: {
      title: 'Mobile menu resources',
      parentId: 'Superior',
      parentPlaceholder: 'Please select Superior',
      displayName: 'Name',
      displayNamePlaceholder: 'Please enter name',
      resourceCode: 'Code',
      resourceCodePlaceholder: 'Please enter the code'
    },
    newButtonPermission: 'New Button Permission',
    parentId: 'Parent Directory',
    parentId0: 'Top-Level Directory',
    displayType: 'Menu Category',
    displayName: 'Menu Name',
    routeMetaIcon: 'Menu Icon',
    routePath: 'Route Path',
    resourceCode: 'Route Name',
    componentPath: 'Component path',
    redirectPath: 'Redirect',
    routeMetaUrl: 'External Link',
    appModule: 'Application Module',
    permissionCodes: 'Menu Permissions',
    otherConfig: 'Other Config',
    status: 'Available',
    hidden: 'Hidden',
    keepAlive: 'Cache',
    ignoreAuth: 'Ignore Authentication',
    buttonPermission: 'Button Permission Configuration',
    buttonPermissionConfig: {
      resourceCode: 'Button Permission Code',
      displayName: 'Button Permission Name',
      permissionCodes: 'Button Permission API'
    },
    displayTypeOptions: {
      catalogue: 'Catalogue',
      menu: 'Menu',
      outsideUrl: 'External URL'
    },
    otherConfigDesc: {
      status: 'Available: Controls whether the menu is effective;',
      hidden: 'Hidden: The menu bar will not be displayed when hidden, but the address can still be accessed;',
      keepAlive: 'Cache: Enables keepAlive on the page, caching the current page;',
      ignoreAuth: 'Ignore Authentication: Current page access does not require authentication.'
    },
    placeholder: {
      displayName: 'Enter menu name',
      routePath: 'Enter route address (e.g: route-path)',
      resourceCode: 'Enter route name (e.g: RouteName)',
      redirectPath: 'Enter redirect',
      routeMetaUrl: 'Enter external link',
      permissionCodes: 'Click to focus then select from the permission list',
      componentName: 'Please enter the component name',
      componentPath: "Component path (e.g: {'@'}/views/user/List.vue)",
      buttonPermissionConfig: {
        _customCode: 'Enter button permission code',
        resourceCode: 'Please select the current button permission code',
        displayName: 'Enter button permission name'
      }
    },
    selectMenuAndOperate: 'Select the left-side menu and perform operations',
    menuConfig: 'Menu Configuration',
    switchButton: 'Input/select to toggle',
    addChild: 'Add child menu',
    componentNameChange: 'Component name has changed. To avoid cache issues, please select again!',
    componentNonExist: 'Component does not exist and cannot load the menu. Please select again!',
    permissionSelect: {
      title: '`Menu` type resource can configure permission API',
      permissionApi: 'Configure Permission API',
      searchPlaceholder: 'Search for required APIs: Supports fuzzy search by title, permission code, and API address',
      tip: 'After selecting the application module, configure permissions'
    }
  },
  role: {
    name: 'Name',
    code: 'Code',
    userList: 'Role Users',
    description: 'Note',
    grantPermission: 'Authorize Permissions',
    permissionList: 'Role Authorization'
  },
  scheduleJob: {
    jobKey: 'Scheduled Task',
    jobKeyAlias: 'Execution Class',
    jobName: 'Task Name',
    cron: 'Cron Expression',
    cronAlias: 'Schedule Rule',
    cronExplain: {
      grammar: 'Format: * * 1 * *?',
      implication: 'Meaning: Seconds Minutes Hours Day Month Week Year'
    },
    onlineEditor: 'Online Editor',
    initStrategy: 'Initialization Strategy',
    initStrategyAlias: 'Execution Strategy',
    paramJson: 'Parameters',
    jobStatus: 'Status',
    saveLog: 'Logs',
    saveLogAlias: 'Log Status',
    jobComment: 'Note',
    open: 'Enable',
    close: 'Disable',
    ready: 'Ready',
    retry: 'Retry Later',
    immediately: 'Confirm to execute once now?'
  },
  scheduleJobLog: {
    startTime: 'Start Time',
    startTimeAlias: 'Execute Time',
    endTime: 'End Time',
    runStatus: 'Execution Status',
    triggerMode: 'Trigger Mode',
    elapsedSeconds: 'Elapsed Time',
    paramJson: 'Parameters',
    executeMsg: 'Execution Result Information',
    success: 'Success',
    fail: 'Failure',
    title: 'Job Log',
    triggerModeOptions: {
      auto: 'Auto',
      manual: 'Manual'
    }
  }
}

export default en
