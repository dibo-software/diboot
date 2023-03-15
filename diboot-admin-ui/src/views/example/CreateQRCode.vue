<script setup lang="ts">
import { reactive } from 'vue'
interface FormItem {
  value: string
  size: number
  foreground: string
}

const form: FormItem = reactive({
  value: 'DIBOOT',
  size: 100,
  foreground: '#000'
})
</script>

<template>
  <el-row :gutter="24" class="p-10">
    <el-col :span="6">
      <el-form :model="form" label-width="80px">
        <el-form-item label="内容">
          <el-input v-model="form.value" placeholder="请输入自定义内容" />
        </el-form-item>
        <el-form-item label="尺寸">
          <el-input-number v-model="form.size" :min="1" placeholder="请输入自定义尺寸" class="w100" />
        </el-form-item>
        <el-form-item label="颜色">
          <el-color-picker v-model="form.foreground" />
        </el-form-item>
      </el-form>
    </el-col>
    <el-col :span="16">
      <qr-code :value="form.value" :size="form.size" :foreground="form.foreground" />
    </el-col>
  </el-row>
</template>

<style scoped>
.w100 {
  width: 100%;
}
</style>
