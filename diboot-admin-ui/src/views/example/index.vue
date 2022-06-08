<script setup lang="ts" name="RichText">
const value = ref(
  '<p>Holle Word!</p><pre style="text-indent: 0; text-align: start;"><code class="language-javascript">alert("Hi!")</code></pre><p><br></p>'
)
</script>

<template>
  <div style="display: flex; height: 100%; margin: 10px">
    <!-- 富文本编辑器 -->
    <rich-editor v-model="value" style="height: calc(100% - 22px); flex: 1; z-index: 1" />

    <div style="flex: 1">
      <div style="margin-left: 10px">
        <h3>富文本回显</h3>
        <el-divider style="margin: 0" />
      </div>
      <el-scrollbar style="height: calc(100% - 85px)">
        <!-- 富文本回显 -->
        <rich-read :value="value" />
      </el-scrollbar>
    </div>
  </div>
</template>

<style scoped></style>
