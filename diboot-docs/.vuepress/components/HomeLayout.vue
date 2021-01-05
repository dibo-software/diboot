<template>
  <div>
    <div class="home">
      <h3 style="position: relative; top: -12px; font-size: 1.6rem; line-height: 1.3; color: #6a8bad; font-weight: normal; text-align: center;">为开发人员打造的轻代码开发平台，高质高效的开发体系</h3>
      <div class="hero">
        <p class="action">
          <a href="https://github.com/dibo-software/diboot/wiki/%E6%96%B0%E6%89%8B%E6%8C%87%E5%8D%97" class="button white">新手指南</a>
          <i class="fa fa-2x fa-play-circle" aria-hidden="true"></i>

          <a href="http://demo.diboot.com/" class="button white" target="_blank">IAM企业版+生成功能 案例展示</a>
          <i class="fa fa-2x fa-play-circle" aria-hidden="true"></i>

          <a href="https://www.bilibili.com/video/BV17V411r7Cc" target="_blank" class="button has-icon">
            <img class="icon" src="../public/play-circle.png" alt="">
            了解 diboot
          </a>
        </p>
      </div>
      <div class="custom content">
        <div class="features">
          <div class="feature col-1">
            <h2>diboot基础组件 - 化繁为简</h2>
            <p>
              <ul>
                  <li>基础组件高效简化，SQL代码减少80%（单表、关联绑定、跨表查询等场景无SQL）</li>
                  <li>常规开发场景的通用封装（关联场景接口、CRUD、常用工具类）</li>
                  <li>全新优化的RBAC权限模型与访问控制（自动提取、智能配置）</li>
                  <li>灵活的扩展能力（扩展多种登录方式、灵活替换用户类）</li>
                  <li>Excel导入支持name-value注解高效转换等</li>
              </ul>
            </p>
          </div>
          <div class="feature col-2">
            <h2>devtools 强大工具 - 以简驭繁</h2>
            <p>
                <ul>
                    <li>使用很简单（引入依赖jar，配置参数后，即可随SpringBoot启动运行）</li>
                    <li>功能很强大（数据结构变更与后端代码联动同步，前后端代码一键生成，自动记录变更SQL）</li>
                    <li>配置很灵活（可按需配置生成代码是否启用`Lombok`、`Swagger`等）</li>
                    <li>代码很标准（devtools标准化了数据结构定义与代码实现，降低维护成本）</li>
                    <li>支持多数据库（MySQL、MariaDB、ORACLE、SQLServer、PostgreSQL）</li>
                </ul>
            </p>
          </div>
        </div>
      </div>
      <div class="custom content">
        <div class="features">
          <img src="../public/structure.png" alt="" style="display: block; margin: 0 auto; width: 100%; height: auto;">
        </div>
      </div>
      <div class="footer">
        <div class="footer-content">
          <div class="footer-item"></div>
          <div class="footer-item">
            <h4>QQ群: 731690096</h4>
            <img src="../public/add_qqqun.png" alt="" width="120">
          </div>
          <div class="footer-item">
            <h4>微信群: wx20201024 (备注diboot)</h4>
            <img src="../public/add_wechat.png" alt="" width="120">
          </div>
          <div class="footer-item">
          </div>
        </div>
        <p class="copy-right">© 2015-2020 <a href="http://www.dibo.ltd">苏州帝博信息技术有限公司</a>
          <br>
          <a class="ba-a" href="https://beian.miit.gov.cn/" target="_blank">
            <u>R
              苏ICP备15013001号
            </u>
          </a>
        </p>
      </div>
    </div>

    <div class="modal-cover" v-show="showModal" @click="closeModal">
      <div class="modal-content" @click="onModalContentClick($event)">
        <video src="https://diboot.oss-cn-shanghai.aliyuncs.com/file/%E5%BC%80%E5%A7%8B%E4%BD%BF%E7%94%A8diboot%E4%B8%8E%E5%85%B3%E8%81%94%E6%BC%94%E7%A4%BA_%E5%AD%97%E5%B9%95.mp4?nsukey=Z8MYZulBJM0GZXYNQgYGJGxRDvYMIWMsGj%2FkV%2BQpZD2aN7hsk7hreW1mXv71kxX7W5Sd61warYIPAAT4xDifYZEv3cQiiQiVW%2BqF%2FqomaxhPm1ht5jV5YkWLGiwcqQKeHmN5jn9%2FRWzaWTNweSZjWuif%2FZIciG6BX8UddZ0klnyyGfrtdB2eWCBAh%2F5n5rMPGdyUhYXAzzDsdPtyBxcY7w%3D%3D"
            class="homeVideo" id="homeVideo" controls>
        </video>
      </div>
    </div>

<!--    <a class="promotions" href="https://www.oschina.net/p/diboot" target="_blank">欢迎帮我们投票，让我们出道！</a>-->
  </div>
</template>

<script>
  export default {
    name: "HomeLayout",
    data () {
      return {
        showModal: false
      }
    },
    methods: {
      showVideo () {
        this.showModal = true
        document.querySelector("#homeVideo").play()
      },
      closeModal () {
        this.showModal = false
        document.querySelector("#homeVideo").pause()
      },
      onModalContentClick(e){
        e.preventDefault()
        e.stopPropagation()
      }
    }
  }
</script>

<style scoped lang="stylus" rel="stylesheet">
.promotions {
  position fixed
  display block
  top: 120px
  left: -15px
  width: 360px;
  line-height: 44px
  text-align center
  text-decoration none
  border: 2px solid #8bd647
  border-radius: 15px
  box-shadow 0 0 10px 3px #8bd647
  font-size: 18px
  font-weight: 500
  background #fff
  color: #6a8bad
}

.home {
  padding: 0 2rem 0;
  max-width: 1160px;
  margin: 0 auto;
  .hero {
    text-align: center;
    .action {
      margin: 1.6rem auto;
    }
    .action-button {
      display: inline-block;
      font-size: 1.2rem;
      color: #fff;
      background-color: #3eaf7c;
      padding: .8rem 1.6rem;
      border-radius: 4px;
      transition: background-color .1s ease;
      border-bottom: #3eaf7c;
    }
    .action-button:hover {
      background-color: #4abf8a;
    }
  }
  .content.custom {
    padding: 0;
    margin: 0;
  }
  .features {
    border-top: 1px solid #eaecef;
    padding: 1.2rem 0;
    margin-top: 2.5rem;
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    align-content: stretch;
    justify-content: space-between;
  }
  .feature {
    flex-grow: 1;
    flex-basis: 30%;
    max-width: 50%;
    h2 {
      font-size: 1.4rem;
      font-weight: 500;
      border-bottom: none;
      padding-bottom: 0;
      color: #3a5169;
    }
    p {
      color: #4e6e8e;
    }
  }
  .footer {
    padding: 1rem 2.5rem .5rem 2.5rem;
    border-top: 1px solid #eaecef;
    text-align: left;
    color: #4e6e8e;
    .footer-content {
      display: flex;
      flex-wrap: wrap;
      align-items: flex-start;
      align-content: stretch;
      justify-content: space-between;
      .footer-item {
        flex-grow: 1;
        flex-basis: 25%;
        max-width: 25%;
        text-align: center;
        h4 {
          font-size: 18px;
          font-weight: 400;
          border-bottom: none;
          padding-bottom: 0;
          color: #3a5169;
          margin: 10px 0;
        }
        ul {
          list-style: none;
          padding: 0;
        }
      }
    }
    p.copy-right {
      padding-top: 20px;
      text-align: center;
      border-top: 1px solid #eaecef;
    }
  }
}


a.button {
  padding: 0.75em 2em;
  border-radius: 2em;
  display: inline-block;
  color: #fff;
  background-color: #4fc08d;
  transition: all 0.15s ease;
  box-sizing: border-box;
  border: 1px solid #4fc08d;
  font-size: 1.05em;
  font-weight: 600;
  letter-spacing: 0.1em;
  min-width: 8em;
  text-align: center;
}
a.button.has-icon {
  position: relative;
  text-indent: 1.4em;
}
a.button.has-icon > .icon {
  position: absolute;
  left: 15px;
  top: 15px;
  margin: 0;
  width: 26px;
}
a.button.white {
  background-color: #fff;
  color: #42b983;
  margin-right: 30px;
}

.modal-cover{
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.2);
  z-index: 1000;
  position: fixed;
  top: 0;
  left: 0;
  text-align: center;
  font-size: 16px;
  box-shadow: 0 0 5px black;
}
.modal-content{
  position: fixed;
  width: 1180px;
  height: 666px;
  top: 50%;
  left: 50%;
  margin-top: -333px;
  margin-left: -590px;
  z-index: 1100;
  background-color: white;
  box-shadow: 0 0 15px black;
}
.homeVideo{
  height: 666px;
}
.donate-image{
  display: block;
  margin: 10px auto;
  width: 500px;
  height: auto;
}
.feature.col-1{
  max-width: 35%;
}
.feature.col-2{
  max-width: 60%;
}
.ba-line{
  margin: 0;
  line-height: 28px;
  text-align: center;
}
.copy-right .ba-a{
  font-size: 16px;
  color: #999;
}
@media screen and (max-width: 750px) {
  .button.has-icon{
    display: block;
    margin-top: 5px;
  }
  .home {
    padding: 0;
  }
  .home .feature{
    padding: 0;
  }
  .feature.col-1{
    max-width: 100%;
  }
  .feature.col-2{
    max-width: 100%;
  }
  .home .footer{
    padding: 1rem 0;
  }
  .home .footer .footer-content .footer-item{
    max-width: 100%;
    flex-basis: 100%;
  }
  .modal-content{
    width: 320px;
    height: 180px;
    margin-top: -90px;
    margin-left: -160px;
  }
  .homeVideo{
    height: 180px;
  }
  a.button.white{
    display: block;
    margin-bottom: 10px;
    margin-right: 0;
  }
}
</style>