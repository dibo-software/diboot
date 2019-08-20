<template>
  <div>
    <div class="home">
      <div class="hero">
        <p class="action">
          <a href="" class="nav-link action-button">起步 →</a>
        </p>
      </div>
      <div class="custom content">
        <div class="features">
          <div class="feature">
            <h2>简明优先</h2>
            <p>对以 markdown 为中心的项目结构，做最简化的配置，帮助你专注于创作。</p>
          </div>
          <div class="feature">
            <h2>Vue 驱动</h2>
            <p>享用 Vue + webpack 开发环境，在 markdown 中使用 Vue 组件，并通过 Vue 开发自定义主题。</p>
          </div>
          <div class="feature">
            <h2>性能高效</h2>
            <p>VuePress 将每个页面生成为预渲染的静态 HTML，每个页面加载之后，然后作为单页面应用程序(SPA)运行。</p>
          </div>
        </div>
      </div>
      <div class="footer">
        © 2015-2019 苏州帝博信息技术有限公司
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: "HomeLayout"
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
    padding: 2.5rem;
    border-top: 1px solid #eaecef;
    text-align: center;
    color: #4e6e8e;
  }
}
</style>