module.exports = {
    title: 'Diboot 轻代码开发平台',
    description: 'Diboot 2.0，更好用的轻代码开发平台',
    head: [
        ['link', {rel: 'icon', href: '/logo.png'}]
    ],
    host: '0.0.0.0',
    port: '9090',
    base: '/devtools-static/',
    themeConfig: {
        // sidebar: 'auto',
        sidebar: {
            '/guide/diboot-core/': [
                {
                    title: 'diboot-core 使用指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-core/安装', '安装'],
                        ['/guide/diboot-core/实体Entity', 'Entity相关'],
                        ['/guide/diboot-core/Service与实现', 'Service相关'],
                        ['/guide/diboot-core/Mapper及自定义', 'Mapper相关'],
                        ['/guide/diboot-core/Controller接口', 'Controller相关'],
                        ['/guide/diboot-core/无SQL关联', '无SQL关联绑定'],
                        ['/guide/diboot-core/查询条件DTO', '查询条件DTO'],
                        ['/guide/diboot-core/常用工具类', '常用工具类']
                    ]
                }
            ],
            '/guide/diboot-shiro/': [
                {
                    title: 'shiro 使用指南',
                    collapsable: true,
                    sidebarDepth: 2,
                    children: [
                        ['/guide/diboot-shiro/安装', '安装'],
                        ['/guide/diboot-shiro/权限设置', '权限设置'],
                        ['/guide/diboot-shiro/权限缓存', '权限缓存'],
                        ['/guide/diboot-shiro/URL配置', 'URL配置'],
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
            ]
        },
        nav: [{
            text: '首页', link: '/'
        }, {
            text: 'core内核 使用指南',
            link: '/guide/diboot-core/安装'
        }, {
            text: 'devtools开发助手 使用指南',
            link: '/guide/diboot-devtools/介绍'
        },{
            text: '捐助Diboot团队',
            link: '/guide/donate/'
        },{
            text: '项目合作',
            link:'http://www.dibo.ltd/contect.html'
         },{
            text: 'Diboot GitHub', link: 'https://github.com/dibo-software/diboot-v2'
        },{
            text: '1.x旧版', link: 'https://www.diboot.com'
        }]
    }
}