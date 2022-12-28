<script setup lang="ts" name="WatermarkExample">
import Logo from '@/assets/logo.png'
import type { Role } from '@/views/system/role/type'

const { getList, dataList } = useList<Role>({
  baseApi: '/iam/role'
})
getList()

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
  <el-card shadow="hover">
    <el-button @click="createTextWatermark">创建文字水印</el-button>
    <el-button @click="createImageWatermark">创建图片水印</el-button>
    <el-button @click="watermarkRef.clear()">清除水印</el-button>
    <watermark ref="watermarkRef" :text="text" :image-url="imageUrl" :image-opacity="0.2">
      <el-table ref="tableRef" class="list-body" :data="dataList" stripe height="100%">
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="code" label="编码" />
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column prop="updateTime" label="更新时间" />
      </el-table>
    </watermark>
  </el-card>
</template>
