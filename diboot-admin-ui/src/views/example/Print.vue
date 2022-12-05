<template>
  <el-main>
    <el-card shadow="never">
      <el-button type="primary" @click="print">普通打印</el-button>
      <dynamic-print handle-upload />
      <div style="height: 20px" />
      <div id="print1" ref="printMain" class="printMain">
        <div class="item">
          打印内容1 <el-icon><el-icon-eleme-filled /></el-icon>
          <p class="no-print">忽略打印</p>
        </div>
      </div>
      <div id="print2" ref="printMain" class="printMain">
        <div class="item">打印内容2</div>
      </div>
    </el-card>
  </el-main>
</template>

<script setup lang="ts">
import { Print } from '@/utils/print'

const print = () => {
  Print([document.getElementById('print1'), document.getElementById('print2')])
}
</script>

<style scoped>
.printMain .item {
  padding: 20px;
  border: 1px solid #409eff;
  margin-bottom: 20px;
  background: #ecf5ff;
  border-radius: 4px;
}
.printMain p {
  margin-top: 20px;
  color: #999;
}
</style>
