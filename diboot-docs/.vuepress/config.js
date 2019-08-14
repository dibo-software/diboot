module.exports = {
    title: 'Diboot2.0',
    description: 'Diboot2.0，更好用的轻代码开发平台',
    base: '/diboot-core-docs/',
    themeConfig: {
        sidebar: 'auto',
        sidebarDepth: 2,
        nav: [{
            text: '首页', link: '/'
        }, {
            text: '学习',
            items: [
                {text: 'diboot-core', link: '/guide/diboot-core/'},
                {text: 'diboot-shiro', link: '/guide/diboot-shiro/'}
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