<template>
  <div class="content">
    <h1>500</h1>
    <h3>服务异常</h3>
    <el-button @click="$router.replace({ name: 'Home' })">首页</el-button>
  </div>
</template>

<style scoped>
.content {
  text-align: center;
  margin: calc(50vh - 100px) 0;
}
</style>
