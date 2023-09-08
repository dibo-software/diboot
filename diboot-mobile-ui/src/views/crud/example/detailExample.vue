<script setup>
import logo from '@/assets/image/logo.png'

const detailData = ref([
  { title: '客户名', value: '帝博软件' },
  { title: '客户类型', value: '企业客户' },
  { title: '联系人', value: '张三' },
  { title: '联系电话', value: '18889889878' },
  { title: '所在城市', value: '江苏省/苏州市/工业园区' },
  { title: '所属行业', value: '软件与信息技术服务业' },
  { title: '来访日期', value: '2023/05/20' },
  { title: '拜访部门', value: '研发部' },
  { title: '是否开车', value: '否' }
])

</script>

<template>
  <van-cell title='来访客户' title-style='font-size: 24px' />
  <van-cell v-for='item in detailData' :title='item.title' :value='item.value' title-style='font-size: 16px' value-class='value-font' />
  <van-cell title='其他资料' value='帝博软件' title-style='font-size: 16px'>
    <template #value>
      <van-image :src="logo" />
    </template>
  </van-cell>
</template>

<style lang='scss'>
.value-font {
  font-size: 16px;
}
</style>
