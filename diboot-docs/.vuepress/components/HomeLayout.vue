<template>
  <div>
    <div class="home">
      <div class="hero">
        <p class="action">
          <a :href="$withBase('/guide/diboot-core/安装.html')" class="button white">快速上手</a>
          <!--
          <i class="fa fa-2x fa-play-circle" aria-hidden="true"></i>

          <a href="javascript:;" class="button has-icon" @click="showVideo">
            <img class="icon" src="/play-circle.png" alt="">
            了解diboot2.0
          </a>
          -->
        </p>
      </div>
      <div class="custom content">
        <div class="features">
          <div class="feature">
            <h2>单表CRUD无SQL</h2>
            <p>借助强大的mybatis-plus，单表CRUD无SQL。</p>
          </div>
          <div class="feature">
            <h2>关联无SQL</h2>
            <p>单表直接关联、中间表多表关联、数据字典关联等，都将无SQL轻松实现。</p>
          </div>
          <div class="feature">
            <h2>自动化工具Devtools</h2>
            <p>支持五大数据库的数据结构维护、数据结构图形化展示、从实体类到Rest接口的代码生成与更新等。</p>
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
        <p class="copy-right">© 2015-2019 苏州帝博信息技术有限公司</p>
      </div>
    </div>

    <div class="modal-cover" v-show="showModal" @click="closeModal">
      <div class="modal-content">
        <iframe width='100%' height='100%' src="//player.bilibili.com/player.html?aid=35545861&cid=62326072&page=1" scrolling="no" border="0" frameborder="no" framespacing="0" allowfullscreen="true"></iframe>
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
  max-width: 960px;
  margin: 0 auto;
  .hero {
    text-align: center;
    .action {
      margin: 1.8rem auto;
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
    max-width: 30%;
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
  z-index: 1100;
  width: 1180px;
  height: 666px;
  background-color: white;
  position: fixed;
  left: 10%;
  top: 10%;
  box-shadow: 0 0 15px black;
}
.donate-image{
  display: block;
  margin: 10px auto;
  width: 500px;
  height: auto;
}
</style>