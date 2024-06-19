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
    <el-button @click="createTextWatermark">{{ $t('watermark.createTextWatermark') }}</el-button>
    <el-button @click="createImageWatermark">{{ $t('watermark.createImageWatermark') }}</el-button>
    <el-button @click="watermarkRef.clear()">{{ $t('watermark.watermarkRef') }}</el-button>
    <watermark ref="watermarkRef" :text="text" :image-url="imageUrl" :image-opacity="0.2">
      <el-table ref="tableRef" class="list-body" :data="dataList" stripe height="100%">
        <el-table-column prop="name" :label="$t('watermark.name')" />
        <el-table-column prop="code" :label="$t('watermark.code')" />
        <el-table-column prop="createTime" :label="$t('baseField.createTime')" />
        <el-table-column prop="updateTime" :label="$t('baseField.updateTime')" />
      </el-table>
    </watermark>
  </el-card>
</template>
