<script setup lang="ts" name="RichText">
const value = ref(
  '<p>Holle Word!</p><pre style="text-indent: 0; text-align: start;"><code class="language-javascript">alert("Hi!")</code></pre><p><br></p>'
)
const title = ref('示例文档')
// 编辑器形式
const doc = ref(false)
</script>

<template>
  <div style="height: calc(100% - 20px); padding: 10px; display: flex; flex-direction: column">
    <el-space style="justify-content: space-between; margin-bottom: 5px">
      <el-alert type="success" :closable="false">
        <span style="font-size: 13px">感谢优秀的 `wangEditor`，项目地址：</span>
        <el-link type="primary" href="https://github.com/wangeditor-team/wangEditor">
          https://github.com/wangeditor-team/wangEditor
        </el-link>
      </el-alert>
      <el-button type="primary" @click="doc = !doc">
        {{ $t('richText.switch') }}：{{ doc ? $t('richText.rich') : $t('richText.doc') }}
      </el-button>
    </el-space>
    <!-- 富文本编辑器 -->
    <rich-editor v-model="value" v-model:title="title" :doc="doc && 'title'" style="flex: 1" />
  </div>
</template>

<style scoped></style>
