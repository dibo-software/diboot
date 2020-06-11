module.exports = {
    title: 'Diboot 轻代码开发平台',
    description: '2.0 - 您的自动化开发助理',
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
                    title: 'IMA-base组件 使用指南',
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
                        ['/guide/diboot-antd-admin/接口请求', '接口请求'],
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
                        ['/guide/diboot-devtools/代码生成与更新', '代码生成与更新']
                    ]
                }
            ],
            '/guide/notes/': [
                {
                    title: '版本&FAQ',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/notes/release', 'Release notes']
                        ['/guide/notes/faq', 'FAQ'],
                        ['/guide/notes/upgrade', '版本升级指南'],
                    ]
                }
            ]
        },
        nav: [{
            text: '首页', link: '/index.html'
        }, {
            text: '基础组件 指南',
            items: [
                { text: 'core基础内核', link: '/guide/diboot-core/安装' },
                { text: 'IAM身份认证', link: '/guide/diboot-iam/介绍' },
                { text: 'File文件组件', link: '/guide/diboot-file/介绍' },
                { text: 'F&Q', link: '/guide/faq/devtools' }
            ]
        }, {
            text: '前端项目 指南',
            items: [
                { text: 'diboot-antd-admin', link: '/guide/diboot-antd-admin/介绍' },
                { text: 'diboot-element-admin', link: '/guide/diboot-element-admin/介绍' }
            ]
        }, {
            text: 'devtools助理 指南',
            link: '/guide/diboot-devtools/介绍'
        }, {
            text: '开发团队',
            items: [
                {
                    text: '项目合作',
                    link:'http://www.dibo.ltd/contect.html'
                },
                {
                    text: '捐助我们',
                    link: '/guide/donate/'
                }
            ]
        }, {
            text: '代码仓库',
            items: [
                {
                    text: 'Gitee',
                    link: 'https://gitee.com/dibo_software/diboot-v2'
                },
                {
                    text: 'GitHub',
                    link: 'https://github.com/dibo-software/diboot-v2'
                }
            ]
        }/*, {
            text: '优秀案例',
            link: '/other/excellentExample'
        }*/]
    }
}