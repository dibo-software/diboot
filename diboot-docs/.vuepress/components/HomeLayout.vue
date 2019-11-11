<template>
  <div>
    <div class="home">
      <div class="hero">
        <p class="action">
          <a :href="$withBase('/guide/diboot-core/安装.html')" class="button white">快速上手</a>

          <i class="fa fa-2x fa-play-circle" aria-hidden="true"></i>
          <a href="javascript:;" class="button has-icon" @click="showVideo">
            <img class="icon" src="../public/play-circle.png" alt="">
            了解 diboot 2.0
          </a>
        </p>
      </div>
      <div class="custom content">
        <div class="features">
          <div class="feature" style="max-width: 35%;">
            <h2>diboot-core 精简高效内核</h2>
            <p>
              <ul>
                  <li>单表CRUD无SQL</li>
                  <li>关联查询无SQL - 注解自动绑定</li>
                  <li>数据字典无SQL - 注解自动绑定</li>
                  <li>Entity/DTO自动转换查询对象</li>
                  <li>常用工具类的最佳实践封装</li>
              </ul>
            </p>
          </div>
          <div class="feature" style="max-width: 60%">
            <h2>diboot-devtools 强大开发助理</h2>
            <p>
                <ul>
                    <li>支持多数据库（MySQL、MariaDB、ORACLE、SQLServer、PostgreSQL）</li>
                    <li>使用很简单（引入依赖jar，配置参数后，即可随SpringBoot启动运行）</li>
                    <li>功能很强大（数据结构变更与代码联动同步，一键生成/更新代码，自动记录变更SQL）</li>
                    <li>配置很灵活（可按需配置生成代码是否启用`Lombok`、`Swagger`、`Shiro`等）</li>
                    <li>代码很标准（devtools标准化了数据结构定义与代码实现，降低维护成本）</li>
                </ul>
            </p>
          </div>
        </div>
      </div>
      <div class="footer">
        <div class="footer-content">
          <div class="footer-item"></div>
          <div class="footer-item">
            <h4>微信（扫码进群）</h4>
            <img src="../public/add_wechat.png" alt="" width="120">
          </div>
          <div class="footer-item">
            <h4>联系我们</h4>
            <ul>
              <li>电话：0512-62988949</li>
              <li>Q Q：281550336</li>
              <li>QQ群：731690096</li>
              <li>邮箱：service@dibo.ltd</li>
            </ul>
          </div>
          <div class="footer-item">
          </div>
        </div>
        <p class="copy-right">© 2015-2019 <a href="http://www.dibo.ltd">苏州帝博信息技术有限公司</a></p>
      </div>
    </div>

    <div class="modal-cover" v-show="showModal" @click="closeModal">
      <div class="modal-content">
        <iframe width='100%' height='100%' src="//player.bilibili.com/player.html?aid=75305809&page=1&high_quality=1" scrolling="no" border="0" frameborder="no" framespacing="0" allowfullscreen="true"> </iframe>
      </div>
    </div>
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
      },
      closeModal () {
        this.showModal = false
      }
    }
  }
</script>

<style scoped lang="stylus" rel="stylesheet">
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
    padding: 1rem 2.5rem 2.5rem;
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
  position: absolute;
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
.donate-image{
  display: block;
  margin: 10px auto;
  width: 500px;
  height: auto;
}
</style>