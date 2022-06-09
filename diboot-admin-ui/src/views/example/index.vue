<script setup lang="ts" name="RichText">
const value = ref(
  '<p>Holle Word!</p><pre style="text-indent: 0; text-align: start;"><code class="language-javascript">alert("Hi!")</code></pre><p><br></p>'
)
</script>

<template>
  <div style="height: calc(100% - 20px); padding: 10px">
    <el-alert type="success" :closable="false">
      感谢优秀的 `wangEditor`，项目地址：
      <el-link type="primary" href="https://github.com/wangeditor-team/wangEditor">
        https://github.com/wangeditor-team/wangEditor
      </el-link>
    </el-alert>

    <!-- 富文本编辑器 -->
    <rich-editor v-model="value" style="height: calc(100% - 39px); flex: 1; z-index: 1" />
  </div>
</template>

<style scoped></style>
