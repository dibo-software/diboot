module.exports = {
    title: 'Diboot 低代码开发平台',
    description: '为开发者打造的高质高效开发体系',
    head: [
        ['link', {rel: 'icon', href: '/logo.png'}]
    ],
    host: 'localhost',
    port: '9090',
    base: '/',
    themeConfig: {
        // sidebar: 'auto',
        sidebar: {
            '/guide/diboot-core/': [
                {
                    title: 'diboot-core 使用指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-core/设计理念', '设计理念'],
                        ['/guide/diboot-core/简介', '简介'],
                        ['/guide/diboot-core/实体Entity', 'Entity相关'],
                        ['/guide/diboot-core/Service接口', 'Service相关'],
                        ['/guide/diboot-core/Mapper及自定义', 'Mapper相关'],
                        ['/guide/diboot-core/Controller接口', 'Controller相关'],
                        ['/guide/diboot-core/无SQL关联绑定', '无SQL关联绑定'],
                        ['/guide/diboot-core/无SQL跨表查询', '无SQL跨表查询'],
                        ['/guide/diboot-core/常用工具类', '常用工具类']
                    ]
                }
            ],
            '/guide/diboot-iam/': [
                {
                    title: 'IMA组件 使用指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-iam/介绍', '介绍'],
                        ['/guide/diboot-iam/开始使用', '开始使用'],
                        ['/guide/diboot-iam/自定义扩展', '自定义扩展'],
                    ]
                }
            ],
            '/guide/diboot-file/': [
                {
                    title: '文件组件 使用指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-file/介绍', '介绍'],
                        ['/guide/diboot-file/开始使用', '开始使用'],
                        ['/guide/diboot-file/自定义扩展', '自定义扩展'],
                    ]
                }
            ],
            '/guide/diboot-scheduler/': [
                {
                    title: '定时任务组件 使用指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-scheduler/介绍', '介绍'],
                        ['/guide/diboot-scheduler/开始使用', '开始使用']
                    ]
                }
            ],
            '/guide/diboot-antd-admin/': [
                {
                    title: 'diboot-antd-admin 项目指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-antd-admin/介绍', '介绍'],
                        ['/guide/diboot-antd-admin/开始使用', '开始使用'],
                        ['/guide/diboot-antd-admin/添加页面', '添加页面'],
                        ['/guide/diboot-antd-admin/权限控制', '权限控制'],
                        ['/guide/diboot-antd-admin/接口请求', '接口请求'],
                        ['/guide/diboot-antd-admin/组件', '组件'],
                        ['/guide/diboot-antd-admin/CRUD快速集成', 'CRUD快速集成'],
                    ]
                }
            ],
            '/guide/diboot-element-admin/': [
                {
                    title: 'diboot-element-admin 指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-element-admin/介绍', '介绍'],
                        ['/guide/diboot-element-admin/开始使用', '开始使用'],
                        ['/guide/diboot-element-admin/添加页面', '添加页面'],
                        ['/guide/diboot-element-admin/权限控制', '权限控制'],
                        ['/guide/diboot-element-admin/接口请求', '接口请求'],
                        ['/guide/diboot-element-admin/组件', '组件'],
                        ['/guide/diboot-element-admin/CRUD快速集成', 'CRUD快速集成'],
                    ]
                }
            ],
            '/guide/diboot-devtools/': [
                {
                    title: 'diboot-devtools 使用指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-devtools/介绍', '介绍'],
                        ['/guide/diboot-devtools/开始使用', '开始使用'],
                        ['/guide/diboot-devtools/数据表管理', '数据表管理'],
                        ['/guide/diboot-devtools/后端代码生成与更新', '后端代码生成与更新'],
                        ['/guide/diboot-devtools/前端功能生成', '前端功能生成']
                    ]
                }
            ],
            '/guide/enterprice/': [
                {
                    title: '企业版',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/enterprice/devtools', 'IAM & devtools 企业版'],
                        ['/guide/enterprice/video', '视频教程']
                    ]
                }
            ],
            '/guide/notes/faq': [
                {
                    title: 'FAQ',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/notes/faq/main', 'FAQ'],
                    ]
                }
            ],
            '/guide/notes/upgrade': [
                {
                    title: '版本升级指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/notes/upgrade/2_1_2升级至2_2_x', '2.1.2升级至2.2.x'],
                        ['/guide/notes/upgrade/2_1_1升级至2_1_2', '2.1.1升级至2.1.2'],
                        ['/guide/notes/upgrade/2_0_x升级至2_1_x', '2.0.x升级至2.1.x'],
                    ]
                }
            ]
        },
        nav: [{
            text: '首页', link: '/index.html'
        }, {
            text: 'diboot 基础',
            items: [
                { text: '后端组件:', items: [
                        { text: ' core基础内核', link: '/guide/diboot-core/设计理念' },
                        { text: ' IAM权限体系', link: '/guide/diboot-iam/介绍' },
                        { text: ' file文件组件', link: '/guide/diboot-file/介绍' },
                        { text: ' scheduler定时任务', link: '/guide/diboot-scheduler/介绍' }
                    ]
                },
                { text: '前端项目:' , items: [
                        { text: 'diboot-antd-admin', link: '/guide/diboot-antd-admin/介绍' },
                        { text: 'diboot-element-admin', link: '/guide/diboot-element-admin/介绍' }
                    ]
                },
                { text: '更多:' , items: [
                        { text: 'F&Q', link: '/guide/notes/faq/main' },
                        { text: '新手指南', link: '/guide/notes/beginner' },
                        { text: '版本升级指南', link: '/guide/notes/upgrade/2_1_2升级至2_2_x' }
                    ]
                }
            ]
        },
        {
            text: 'devtools 工具',
            link: '/guide/diboot-devtools/介绍'
        },
        {
            text: 'cloud 微服务版',
            link: '/guide/diboot-cloud/introduce'
        },
        {
            text: '商业服务',
            link: '/guide/enterprice/service'
        },
        {
            text: '视频教程',
            link: '/guide/enterprice/video'
        },
        {
            text: '源码与团队',
            items: [
                {
                    text: '更新日志',
                    link: 'https://github.com/dibo-software/diboot/releases'
                },
                {
                    text: 'GitHub',
                    link: 'https://github.com/dibo-software/diboot'
                },
                {
                    text: 'Gitee',
                    link: 'https://gitee.com/dibo_software/diboot'
                },
                {
                    text: '团队&合作',
                    link:'http://www.dibo.ltd'
                }
            ]
        }]
    }
}