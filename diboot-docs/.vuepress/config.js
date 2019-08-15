module.exports = {
    title: 'Diboot2.0',
    description: 'Diboot2.0，更好用的轻代码开发平台',
    head: [
        ['link', {rel: 'icon', href: '/logo.png'}]
    ],
    host: '0.0.0.0',
    port: '9090',
    base: '/docs/',
    themeConfig: {
        sidebar: 'auto',
        sidebarDepth: 2,
        nav: [{
            text: '首页', link: '/'
        }, {
            text: '学习',
            items: [
                {text: 'diboot-core指南', link: '/guide/diboot-core/'},
                {text: 'diboot-shiro指南', link: '/guide/diboot-shiro/'},
                {text: 'diboot-devtools指南', link: '/guide/diboot-devtools/'}
            ]
        }, {
            text: 'API',
            items: [
                {text: 'diboot-core', link: '/api/diboot-core/'}
            ]
        },{
            text: '1.x', link: 'https://diboot.com'
        }, {
            text: 'GitHub', link: 'https://github.com/dibo-software/diboot-v2'
        }]
    }
}