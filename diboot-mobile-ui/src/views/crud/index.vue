<template>
  <van-grid :column-num="3" style="padding: 0 15px; background-color: var(--van-background-2)">
    <van-grid-item icon="bars" text="列表示例" @click="$router.push({ name: 'List' })"></van-grid-item>
    <van-grid-item icon="orders-o" text="表单示例" @click="$router.push({ name: 'Form' })"></van-grid-item>
    <van-grid-item icon="notes-o" text="详情示例" @click="$router.push({ name: 'Detail' })"></van-grid-item>
  </van-grid>
</template>
