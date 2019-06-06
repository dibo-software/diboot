## 我们使用了什么？
* 这里的所有文档都使用vuepress这个工具来处理，关于vuepress的细节可以阅览[官方文档](https://vuepress.vuejs.org/zh/)。
* 我们采用markdown来编写项目文档，如果您也要参与文档的贡献，也需要先了解markdown相关内容。
## 我们应该怎样开始？
1. 假设您本地已经安装了node环境，npm与yarn至少有其一了，那么打开命令行，我们先运行以下命令全局安装（已经安装过的不必再安装）vuepress:
    ```bash
    yarn global add vuepress 
    # 或者：
    npm install -g vuepress
    ```
2. 拉取该项目到本地，在命令行打开该目录下的相关文档目录。
3. 运行以下命令，开始启动一个文档的server：
    ```bash
    yarn serve
    # 或者：
    npm run serve
    ```
## 我们怎样存放静态文件？
* 我们的图片文件以及其他类型的文件，建议存放在相应文档项目下的.vuepress/public/目录下。
## 我们如何引入存放的静态文件？
* 我们统一使用$withBase()方法来获取存放在.vuepress/public目录下的静态文件，示例如下：
    ```markdown
    ![图片]($withBase('/image.png'))
    ```