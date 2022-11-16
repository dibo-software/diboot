import type { RestPermission } from '@/views/system/resource/type'

export default {
  test: [
    {
      name: '子表',
      code: 'Child',
      apiPermissionList: [
        {
          code: 'Child:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/child',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'Child:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/child/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'Child:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/child',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'Child:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/child/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        },
        {
          code: 'Child:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/child/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        }
      ]
    }
  ],
  system: [
    {
      name: '数据字典',
      code: 'Dictionary',
      apiPermissionList: [
        {
          code: 'Dictionary:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/dictionary',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'Dictionary:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/dictionary/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'Dictionary:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/dictionary',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'Dictionary:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/dictionary/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        },
        {
          code: 'Dictionary:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/dictionary/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        }
      ]
    },
    {
      name: '消息通知',
      code: 'Message',
      apiPermissionList: [
        {
          code: 'Message:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/message',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'Message:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/message/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        }
      ]
    },
    {
      name: '消息通知模版',
      code: 'MessageTemplate',
      apiPermissionList: [
        {
          code: 'MessageTemplate:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/message-template',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'MessageTemplate:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/message-template/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'MessageTemplate:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/message-template',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'MessageTemplate:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/message-template/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        },
        {
          code: 'MessageTemplate:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/message-template/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        }
      ]
    },
    {
      name: '父表',
      code: 'Parent',
      apiPermissionList: [
        {
          code: 'Parent:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/parent',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'Parent:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/parent/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'Parent:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/parent',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'Parent:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/parent/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        },
        {
          code: 'Parent:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/parent/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        }
      ]
    },
    {
      name: '定时任务',
      code: 'ScheduleJob',
      apiPermissionList: [
        {
          code: 'ScheduleJob:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/schedule-job',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'ScheduleJob:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/schedule-job',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'ScheduleJob:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/schedule-job/{id}',
              label: '更新'
            },
            {
              method: 'PUT',
              uri: '/schedule-job/{id}/{action}',
              label: '更新'
            }
          ],
          label: '更新,更新'
        },
        {
          code: 'ScheduleJob:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/schedule-job/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        },
        {
          code: 'ScheduleJob:EXECUTE_ONCE_JOB',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/schedule-job/executeOnce/{id}',
              label: '执行一次定时任务'
            }
          ],
          label: '执行一次定时任务'
        },
        {
          code: 'ScheduleJob:JOB_LOG_LIST',
          apiUriList: [
            {
              method: 'GET',
              uri: '/schedule-job/log',
              label: '定时日志列表'
            }
          ],
          label: '定时日志列表'
        },
        {
          code: 'ScheduleJob:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/schedule-job/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'ScheduleJob:JOB_LOG_DETAIL',
          apiUriList: [
            {
              method: 'GET',
              uri: '/schedule-job/log/{id}',
              label: '定时日志详情'
            }
          ],
          label: '定时日志详情'
        },
        {
          code: 'ScheduleJob:JOB_LOG_DELETE',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/schedule-job/log/{id}',
              label: '删除任务日志'
            }
          ],
          label: '删除任务日志'
        }
      ]
    },
    {
      name: '系统配置',
      code: 'SystemConfig',
      apiPermissionList: [
        {
          code: 'SystemConfig:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/system-config/typeList',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'SystemConfig:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/system-config/{type}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'SystemConfig:update',
          apiUriList: [
            {
              method: 'POST',
              uri: '/system-config',
              label: '更新'
            },
            {
              method: 'DELETE',
              uri: '/system-config/{type}/{prop}',
              label: '重置'
            },
            {
              method: 'DELETE',
              uri: '/system-config/{type}',
              label: '重置'
            },
            {
              method: 'POST',
              uri: '/system-config/{type}',
              label: '测试'
            }
          ],
          label: '更新,重置,重置,测试'
        }
      ]
    },
    {
      name: '文件记录',
      code: 'FileRecord',
      apiPermissionList: [
        {
          code: 'FileRecord:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/file-record',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'FileRecord:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/file-record/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'FileRecord:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/file-record/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        }
      ]
    },
    {
      name: '登录日志',
      code: 'IamLoginTrace',
      apiPermissionList: [
        {
          code: 'IamLoginTrace:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/login-trace',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        }
      ]
    },
    {
      name: '操作日志',
      code: 'IamOperationLog',
      apiPermissionList: [
        {
          code: 'IamOperationLog:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/operation-log',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'IamOperationLog:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/operation-log/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        }
      ]
    },
    {
      name: '组织机构',
      code: 'IamOrg',
      apiPermissionList: [
        {
          code: 'IamOrg:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/org/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'IamOrg:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/iam/org',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'IamOrg:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/iam/org/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        },
        {
          code: 'IamOrg:sort',
          apiUriList: [
            {
              method: 'POST',
              uri: '/iam/org/sort',
              label: '列表排序'
            }
          ],
          label: '列表排序'
        },
        {
          code: 'IamOrg:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/org',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'IamOrg:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/iam/org/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        },
        {
          code: 'IamOrg:tree',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/org/tree',
              label: '获取组织树'
            }
          ],
          label: '获取组织树'
        },
        {
          code: 'IamOrg:subTree',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/org/tree/{parentNodeId}',
              label: '查看子组织树'
            }
          ],
          label: '查看子组织树'
        },
        {
          code: 'IamOrg:children',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/org/children-list/{parentNodeId}',
              label: '获取子组织列表'
            }
          ],
          label: '获取子组织列表'
        }
      ]
    },
    {
      name: '岗位',
      code: 'IamPosition',
      apiPermissionList: [
        {
          code: 'IamPosition:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/position',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'IamPosition:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/iam/position/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        },
        {
          code: 'IamPosition:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/position/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'IamPosition:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/iam/position',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'IamPosition:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/iam/position/{id}',
              label: '更新'
            },
            {
              method: 'POST',
              uri: '/iam/position/batch-update-user-position-relations',
              label: '设置用户岗位关系'
            }
          ],
          label: '更新,设置用户岗位关系'
        }
      ]
    },
    {
      name: '系统资源权限',
      code: 'IamResource',
      apiPermissionList: [
        {
          code: 'IamResource:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/resource',
              label: '查看列表'
            },
            {
              method: 'GET',
              uri: '/iam/resource/getMenuTreeList',
              label: '查看列表'
            }
          ],
          label: '查看列表,查看列表'
        },
        {
          code: 'IamResource:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/resource/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'IamResource:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/iam/resource',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'IamResource:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/iam/resource/{id}',
              label: '更新'
            },
            {
              method: 'POST',
              uri: '/iam/resource/sort',
              label: '列表排序'
            }
          ],
          label: '更新,列表排序'
        },
        {
          code: 'IamResource:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/iam/resource/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        }
      ]
    },
    {
      name: '角色',
      code: 'IamRole',
      apiPermissionList: [
        {
          code: 'IamRole:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/role',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'IamRole:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/role/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'IamRole:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/iam/role',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'IamRole:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/iam/role/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        },
        {
          code: 'IamRole:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/iam/role/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        }
      ]
    },
    {
      name: '用户',
      code: 'IamUser',
      apiPermissionList: [
        {
          code: 'IamUser:list',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/user',
              label: '查看列表'
            }
          ],
          label: '查看列表'
        },
        {
          code: 'IamUser:detail',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/user/{id}',
              label: '查看详情'
            }
          ],
          label: '查看详情'
        },
        {
          code: 'IamUser:create',
          apiUriList: [
            {
              method: 'POST',
              uri: '/iam/user',
              label: '创建'
            }
          ],
          label: '创建'
        },
        {
          code: 'IamUser:update',
          apiUriList: [
            {
              method: 'PUT',
              uri: '/iam/user/{id}',
              label: '更新'
            }
          ],
          label: '更新'
        },
        {
          code: 'IamUser:delete',
          apiUriList: [
            {
              method: 'DELETE',
              uri: '/iam/user/{id}',
              label: '删除'
            }
          ],
          label: '删除'
        }
      ]
    },
    {
      name: '用户Excel上传下载',
      code: 'IamUserExcel',
      apiPermissionList: [
        {
          code: 'IamUserExcel:import',
          apiUriList: [
            {
              method: 'POST',
              uri: '/iam/user/excel/preview-save',
              label: '导入'
            },
            {
              method: 'POST',
              uri: '/iam/user/excel/preview',
              label: 'excel预览'
            },
            {
              method: 'GET',
              uri: '/iam/user/excel/download-example',
              label: '下载示例文件'
            },
            {
              method: 'POST',
              uri: '/iam/user/excel/upload',
              label: '导入'
            }
          ],
          label: '导入,excel预览,下载示例文件,导入'
        },
        {
          code: 'IamUserExcel:export',
          apiUriList: [
            {
              method: 'GET',
              uri: '/iam/user/excel/export',
              label: '导出'
            }
          ],
          label: '导出'
        }
      ]
    }
  ]
} as Record<string, RestPermission[]>
