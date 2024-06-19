<template>
  <div class="content">
    <h1>500</h1>
    <h3>{{ $t('exception.service500') }}</h3>
    <el-button @click="$router.replace({ name: 'Home' })">{{ $t('exception.backHome') }}</el-button>
  </div>
</template>

<style scoped>
.content {
  text-align: center;
  margin: calc(50vh - 100px) 0;
}
</style>
