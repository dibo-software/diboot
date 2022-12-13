<script setup lang="ts" name="WatermarkExample">
import Logo from '@/assets/logo.png'
const tableData = [
  {
    date: '2016-05-03',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  },
  {
    date: '2016-05-02',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  },
  {
    date: '2016-05-04',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  },
  {
    date: '2016-05-01',
    name: 'Tom',
    address: 'No. 189, Grove St, Los Angeles'
  }
]
const dataList = ref<Array<object>>([])
tableData.forEach(() => {
  dataList.value.push(...tableData)
})

const text = ref()
const imageUrl = ref()
const watermarkRef = ref()
const createTextWatermark = () => {
  text.value = 'Diboot'
  imageUrl.value = ''
  nextTick(() => watermarkRef.value.create())
}
const createImageWatermark = () => {
  text.value = ''
  imageUrl.value = Logo
  nextTick(() => watermarkRef.value.create())
}
</script>

<template>
  <el-main>
    <el-card shadow="never">
      <el-button @click="createTextWatermark">创建文字水印</el-button>
      <el-button @click="createImageWatermark">创建图片水印</el-button>
      <el-button @click="watermarkRef.clear()">清除水印</el-button>
      <watermark ref="watermarkRef" :text="text" :image-url="imageUrl" :image-opacity="0.2">
        <el-table :data="dataList" style="width: 100%">
          <el-table-column prop="date" label="Date" width="180" />
          <el-table-column prop="name" label="Name" width="180" />
          <el-table-column prop="address" label="Address" />
        </el-table>
      </watermark>
    </el-card>
  </el-main>
</template>
