<script setup lang="ts" name="RichText">
const value = ref(
  '<p>Holle Word!</p><pre style="text-indent: 0; text-align: start;"><code class="language-javascript">alert("Hi!")</code></pre><p><br></p>'
)
const title = ref('示例文档')
// 编辑器形式
const doc = ref(false)
</script>

<template>
  <div style="height: calc(100% - 20px); padding: 10px">
    <el-alert type="success" :closable="false">
      <span style="font-size: 13px">感谢优秀的 `wangEditor`，项目地址：</span>
      <el-link type="primary" href="https://github.com/wangeditor-team/wangEditor">
        https://github.com/wangeditor-team/wangEditor
      </el-link>

      <el-button link type="danger" style="position: absolute; right: 15px; margin-top: -5px" @click="doc = !doc">
        切换为：{{ doc ? '富文本编辑器' : '文档编辑器' }}
      </el-button>
    </el-alert>

    <!-- 富文本编辑器 -->
    <rich-editor v-model="value" v-model:title="title" :doc="doc && 'title'" style="height: calc(100% - 41px)" />
  </div>
</template>

<style scoped></style>
