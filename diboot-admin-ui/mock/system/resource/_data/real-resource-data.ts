export default [
  {
    id: '10040',
    parentId: '0',
    displayType: 'CATALOGUE',
    displayName: '组织机构',
    routePath: 'orgStructure',
    resourceCode: 'orgStructure',
    status: 'A',
    sortId: '90',
    routeMeta: {
      icon: 'Element:UserFilled'
    },
    children: [
      {
        id: '10041',
        appModule: 'system',
        parentId: '10040',
        displayType: 'MENU',
        displayName: '组织机构管理',
        routePath: 'org',
        resourceCode: 'orgIndex',
        permissionCode: 'IamOrg:read',
        status: 'A',
        sortId: '1',
        routeMeta: {
          icon: 'Element:Folder',
          componentPath: '@/views/orgStructure/org/index.vue'
        },
        parentDisplayName: '组织机构',
        permissionList: [
          {
            id: '10042',
            parentId: '10041',
            displayType: 'PERMISSION',
            displayName: '排序',
            resourceCode: 'sort',
            permissionCode: 'IamOrg:write',
            status: 'A',
            sortId: '106',
            routeMeta: {},
            permissionCodes: ['IamOrg:write']
          },
          {
            id: '10043',
            parentId: '10041',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'IamOrg:write',
            status: 'A',
            sortId: '105',
            routeMeta: {},
            permissionCodes: ['IamOrg:write']
          },
          {
            id: '10044',
            parentId: '10041',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'IamOrg:write,IamOrg:read',
            status: 'A',
            sortId: '104',
            routeMeta: {},
            permissionCodes: ['IamOrg:write', 'IamOrg:read']
          },
          {
            id: '10045',
            parentId: '10041',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'IamOrg:write',
            status: 'A',
            sortId: '103',
            routeMeta: {},
            permissionCodes: ['IamOrg:write']
          },
          {
            id: '10046',
            parentId: '10041',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'IamOrg:read',
            status: 'A',
            sortId: '102',
            routeMeta: {},
            permissionCodes: ['IamOrg:read']
          }
        ],
        permissionCodes: ['IamOrg:read']
      },
      {
        id: '10047',
        parentId: '10040',
        displayType: 'MENU',
        displayName: '岗位管理',
        routePath: 'position',
        resourceCode: 'PositionList',
        permissionCode: 'IamPosition:read',
        status: 'A',
        sortId: '2',
        routeMeta: {
          icon: 'Element:Avatar',
          componentPath: '@/views/orgStructure/position/List.vue'
        },
        parentDisplayName: '组织机构',
        permissionList: [
          {
            id: '10048',
            parentId: '10047',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'IamPosition:write',
            status: 'A',
            sortId: '112',
            routeMeta: {},
            permissionCodes: ['IamPosition:write']
          },
          {
            id: '10049',
            parentId: '10047',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'IamPosition:read',
            status: 'A',
            sortId: '111',
            routeMeta: {},
            permissionCodes: ['IamPosition:read']
          },
          {
            id: '10050',
            parentId: '10047',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'IamPosition:write,IamPosition:read',
            status: 'A',
            sortId: '110',
            routeMeta: {},
            permissionCodes: ['IamPosition:write', 'IamPosition:read']
          },
          {
            id: '10051',
            parentId: '10047',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'IamPosition:write',
            status: 'A',
            sortId: '108',
            routeMeta: {},
            permissionCodes: ['IamPosition:write']
          }
        ],
        permissionCodes: ['IamPosition:read']
      },
      {
        id: '10052',
        parentId: '10040',
        displayType: 'MENU',
        displayName: '组织人员管理',
        routePath: 'user',
        resourceCode: 'UserIndex',
        permissionCode: 'IamOrg:read,IamUser:read',
        status: 'A',
        sortId: '3',
        routeMeta: {
          icon: 'Element:User',
          componentPath: '@/views/orgStructure/user/index.vue'
        },
        parentDisplayName: '组织机构',
        permissionList: [
          {
            id: '10053',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'IamUser:write',
            status: 'A',
            sortId: '40',
            routeMeta: {},
            permissionCodes: ['IamUser:write']
          },
          {
            id: '10054',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'IamUser:write,IamUser:read',
            status: 'A',
            sortId: '39',
            routeMeta: {},
            permissionCodes: ['IamUser:write', 'IamUser:read']
          },
          {
            id: '10055',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'IamUser:write',
            status: 'A',
            sortId: '38',
            routeMeta: {},
            permissionCodes: ['IamUser:write']
          },
          {
            id: '10056',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'IamUser:read',
            status: 'A',
            sortId: '37',
            routeMeta: {},
            permissionCodes: ['IamUser:read']
          },
          {
            id: '10057',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '导入',
            resourceCode: 'import',
            permissionCode: 'IamUserExcel:import',
            status: 'A',
            sortId: '36',
            routeMeta: {},
            permissionCodes: ['IamUserExcel:import']
          },
          {
            id: '10058',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '导出',
            resourceCode: 'export',
            permissionCode: 'IamUserExcel:export',
            status: 'A',
            sortId: '35',
            routeMeta: {},
            permissionCodes: ['IamUserExcel:export']
          },
          {
            id: '10059',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '人员岗位设置',
            resourceCode: 'position',
            permissionCode: 'IamPosition:write,IamPosition:read',
            status: 'A',
            sortId: '34',
            routeMeta: {},
            permissionCodes: ['IamPosition:write', 'IamPosition:read']
          },
          {
            id: '10060',
            parentId: '10052',
            displayType: 'PERMISSION',
            displayName: '添加岗位',
            resourceCode: 'addPosition',
            permissionCode: 'IamPosition:write,IamPosition:read',
            status: 'A',
            sortId: '33',
            routeMeta: {},
            permissionCodes: ['IamPosition:write', 'IamPosition:read']
          }
        ],
        permissionCodes: ['IamOrg:read', 'IamUser:read']
      }
    ]
  },
  {
    id: '10000',
    parentId: '0',
    displayType: 'CATALOGUE',
    displayName: '系统管理',
    routePath: 'system',
    resourceCode: 'system',
    status: 'A',
    sortId: '95',
    routeMeta: {
      icon: 'Element:Tools'
    },
    children: [
      {
        id: '10001',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '数据字典管理',
        routePath: 'dictionary',
        resourceCode: 'DictionaryList',
        permissionCode: 'Dictionary:read',
        status: 'A',
        sortId: '10',
        routeMeta: {
          icon: 'Element:Collection',
          componentPath: '@/views/system/dictionary/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10002',
            parentId: '10001',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'Dictionary:read',
            status: 'A',
            sortId: '6',
            routeMeta: {},
            permissionCodes: ['Dictionary:read']
          },
          {
            id: '10003',
            parentId: '10001',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'Dictionary:write',
            status: 'A',
            sortId: '5',
            routeMeta: {},
            permissionCodes: ['Dictionary:write']
          },
          {
            id: '10004',
            parentId: '10001',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'Dictionary:write,Dictionary:read',
            status: 'A',
            sortId: '4',
            routeMeta: {},
            permissionCodes: ['Dictionary:write', 'Dictionary:read']
          },
          {
            id: '10005',
            parentId: '10001',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'Dictionary:write',
            status: 'A',
            sortId: '3',
            routeMeta: {},
            permissionCodes: ['Dictionary:write']
          }
        ],
        permissionCodes: ['Dictionary:read']
      },
      {
        id: '10006',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '资源权限管理',
        routePath: 'resourcePermission',
        resourceCode: 'Resource',
        permissionCode: 'IamResource:read',
        status: 'A',
        sortId: '20',
        routeMeta: {
          icon: 'Element:Menu',
          componentPath: '@/views/system/resource/index.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10007',
            parentId: '10006',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'IamResource:read',
            status: 'A',
            sortId: '23',
            routeMeta: {},
            permissionCodes: ['IamResource:read']
          },
          {
            id: '10008',
            parentId: '10006',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'IamResource:write',
            status: 'A',
            sortId: '21',
            routeMeta: {},
            permissionCodes: ['IamResource:write']
          },
          {
            id: '10009',
            parentId: '10006',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'IamResource:write,IamResource:read',
            status: 'A',
            sortId: '20',
            routeMeta: {},
            permissionCodes: ['IamResource:write', 'IamResource:read']
          },
          {
            id: '10010',
            parentId: '10006',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'IamResource:write',
            status: 'A',
            sortId: '19',
            routeMeta: {},
            permissionCodes: ['IamResource:write']
          },
          {
            id: '10011',
            parentId: '10006',
            displayType: 'PERMISSION',
            displayName: '排序',
            resourceCode: 'sort',
            permissionCode: 'IamResource:write',
            status: 'A',
            sortId: '18',
            routeMeta: {},
            permissionCodes: ['IamResource:write']
          }
        ],
        permissionCodes: ['IamResource:read']
      },
      {
        id: '10012',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '角色权限管理',
        routePath: 'role',
        resourceCode: 'RoleList',
        permissionCode: 'IamRole:read',
        status: 'A',
        sortId: '30',
        routeMeta: {
          icon: 'Element:Avatar',
          componentPath: '@/views/system/role/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10013',
            parentId: '10012',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'IamRole:read',
            status: 'A',
            sortId: '16',
            routeMeta: {},
            permissionCodes: ['IamRole:read']
          },
          {
            id: '10014',
            parentId: '10012',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'IamRole:write',
            status: 'A',
            sortId: '15',
            routeMeta: {},
            permissionCodes: ['IamRole:write']
          },
          {
            id: '10015',
            parentId: '10012',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'IamRole:write,IamRole:read,IamResource:read',
            status: 'A',
            sortId: '14',
            routeMeta: {},
            permissionCodes: ['IamRole:write', 'IamRole:read', 'IamResource:read']
          },
          {
            id: '10016',
            parentId: '10012',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'IamRole:write',
            status: 'A',
            sortId: '13',
            routeMeta: {},
            permissionCodes: ['IamRole:write']
          }
        ],
        permissionCodes: ['IamRole:read']
      },
      {
        id: '10017',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '定时任务管理',
        routePath: 'scheduleJob',
        resourceCode: 'ScheduleJob',
        permissionCode: 'ScheduleJob:read',
        status: 'A',
        sortId: '40',
        routeMeta: {
          icon: 'Element:AlarmClock',
          componentPath: '@/views/system/schedule-job/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10018',
            parentId: '10017',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'ScheduleJob:write',
            status: 'A',
            sortId: '7',
            routeMeta: {},
            permissionCodes: ['ScheduleJob:write']
          },
          {
            id: '10019',
            parentId: '10017',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'ScheduleJob:write,ScheduleJob:read',
            status: 'A',
            sortId: '6',
            routeMeta: {},
            permissionCodes: ['ScheduleJob:write', 'ScheduleJob:read']
          },
          {
            id: '10020',
            parentId: '10017',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'ScheduleJob:write',
            status: 'A',
            sortId: '5',
            routeMeta: {},
            permissionCodes: ['ScheduleJob:write']
          },
          {
            id: '10021',
            parentId: '10017',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'ScheduleJob:read',
            status: 'A',
            sortId: '4',
            routeMeta: {},
            permissionCodes: ['ScheduleJob:read']
          },
          {
            id: '10022',
            parentId: '10017',
            displayType: 'PERMISSION',
            displayName: '运行一次',
            resourceCode: 'executeOnce',
            permissionCode: 'ScheduleJob:write',
            status: 'A',
            sortId: '3',
            routeMeta: {},
            permissionCodes: ['ScheduleJob:write']
          },
          {
            id: '10023',
            parentId: '10017',
            displayType: 'PERMISSION',
            displayName: '日志记录',
            resourceCode: 'logList',
            permissionCode: 'ScheduleJob:read',
            status: 'A',
            sortId: '2',
            routeMeta: {},
            permissionCodes: ['ScheduleJob:read']
          },
          {
            id: '10024',
            parentId: '10017',
            displayType: 'PERMISSION',
            displayName: '日志删除',
            resourceCode: 'logDelete',
            permissionCode: 'ScheduleJob:write',
            status: 'A',
            sortId: '1',
            routeMeta: {},
            permissionCodes: ['ScheduleJob:write']
          }
        ],
        permissionCodes: ['ScheduleJob:read']
      },
      {
        id: '10025',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '消息模板管理',
        routePath: 'messageTemplate',
        resourceCode: 'MessageTemplate',
        permissionCode: 'MessageTemplate:read',
        status: 'A',
        sortId: '50',
        routeMeta: {
          icon: 'Element:ChatLineSquare',
          componentPath: '@/views/system/message-template/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10026',
            parentId: '10025',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'MessageTemplate:read',
            status: 'A',
            sortId: '16',
            routeMeta: {},
            permissionCodes: ['MessageTemplate:read']
          },
          {
            id: '10027',
            parentId: '10025',
            displayType: 'PERMISSION',
            displayName: '新建',
            resourceCode: 'create',
            permissionCode: 'MessageTemplate:write',
            status: 'A',
            sortId: '15',
            routeMeta: {},
            permissionCodes: ['MessageTemplate:write']
          },
          {
            id: '10028',
            parentId: '10025',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'MessageTemplate:write,MessageTemplate:read',
            status: 'A',
            sortId: '14',
            routeMeta: {},
            permissionCodes: ['MessageTemplate:write', 'MessageTemplate:read']
          },
          {
            id: '10029',
            parentId: '10025',
            displayType: 'PERMISSION',
            displayName: '删除',
            resourceCode: 'delete',
            permissionCode: 'MessageTemplate:write',
            status: 'A',
            sortId: '13',
            routeMeta: {},
            permissionCodes: ['MessageTemplate:write']
          }
        ],
        permissionCodes: ['MessageTemplate:read']
      },
      {
        id: '10030',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '消息记录管理',
        routePath: 'message',
        resourceCode: 'Message',
        permissionCode: 'Message:read',
        status: 'A',
        sortId: '60',
        routeMeta: {
          icon: 'Element:ChatDotRound',
          componentPath: '@/views/system/message/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10031',
            parentId: '10030',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'Message:read',
            status: 'A',
            sortId: '16',
            routeMeta: {},
            permissionCodes: ['Message:read']
          }
        ],
        permissionCodes: ['Message:read']
      },
      {
        id: '10032',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '文件记录管理',
        routePath: 'fileRecord',
        resourceCode: 'FileRecord',
        permissionCode: 'FileRecord:read',
        status: 'A',
        sortId: '70',
        routeMeta: {
          icon: 'Element:FolderOpened',
          componentPath: '@/views/system/fileRecord/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10033',
            parentId: '10032',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'FileRecord:read',
            status: 'A',
            sortId: '16',
            routeMeta: {},
            permissionCodes: ['FileRecord:read']
          },
          {
            id: '10034',
            parentId: '10032',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'FileRecord:write,FileRecord:read',
            status: 'A',
            sortId: '14',
            routeMeta: {},
            permissionCodes: ['FileRecord:write', 'FileRecord:read']
          }
        ],
        permissionCodes: ['FileRecord:read']
      },
      {
        id: '10035',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '系统配置管理',
        routePath: 'config',
        resourceCode: 'SystemConfig',
        permissionCode: 'SystemConfig:read',
        status: 'A',
        sortId: '80',
        routeMeta: {
          icon: 'Element:Setting',
          componentPath: '@/views/system/config/index.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10036',
            parentId: '10035',
            displayType: 'PERMISSION',
            displayName: '更新',
            resourceCode: 'update',
            permissionCode: 'SystemConfig:write',
            status: 'A',
            sortId: '1',
            routeMeta: {},
            permissionCodes: ['SystemConfig:write']
          }
        ],
        permissionCodes: ['SystemConfig:read']
      },
      {
        id: '10037',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '操作日志查看',
        routePath: 'operationLog',
        resourceCode: 'OperationLog',
        permissionCode: 'IamOperationLog:read',
        status: 'A',
        sortId: '90',
        routeMeta: {
          icon: 'Element:Pointer',
          componentPath: '@/views/system/operation-log/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionList: [
          {
            id: '10038',
            parentId: '10037',
            displayType: 'PERMISSION',
            displayName: '详情',
            resourceCode: 'detail',
            permissionCode: 'IamOperationLog:read',
            status: 'A',
            sortId: '1',
            routeMeta: {},
            permissionCodes: ['IamOperationLog:read']
          }
        ],
        permissionCodes: ['IamOperationLog:read']
      },
      {
        id: '10039',
        parentId: '10000',
        displayType: 'MENU',
        displayName: '登录日志查看',
        routePath: 'loginTrace',
        resourceCode: 'LoginTrace',
        permissionCode: 'IamLoginTrace:read',
        status: 'A',
        sortId: '100',
        routeMeta: {
          icon: 'Element:Finished',
          componentPath: '@/views/system/login-trace/List.vue'
        },
        parentDisplayName: '系统管理',
        permissionCodes: ['IamLoginTrace:read']
      }
    ]
  }
]
