<template>
  <van-grid :column-num="3" style="padding: 0 15px; background-color: var(--van-background-2)">
    <van-grid-item icon="bars" text="列表示例" @click="$router.push({ name: 'ListExample' })"></van-grid-item>
    <van-grid-item icon="orders-o" text="表单示例" @click="$router.push({ name: 'FormExample' })"></van-grid-item>
    <van-grid-item icon="notes-o" text="详情示例" @click="$router.push({ name: 'DetailExample' })"></van-grid-item>
  </van-grid>
</template>
