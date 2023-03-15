<script setup lang="ts">
import { useStore } from '@/store/echarts'
const store = useStore()

const cardList = [
  {
    name: '折线图',
    type: 'line',
    optionType: 'lineOptions'
  },
  {
    name: '柱形图',
    type: 'bar',
    optionType: 'barOptions'
  },
  {
    name: '折柱混合图',
    type: 'lineBar',
    optionType: 'lineBarOptions'
  },
  {
    name: '饼图',
    type: 'pie',
    optionType: 'pieOptions'
  },
  {
    name: '雷达图',
    type: 'radar',
    optionType: 'radarOptions'
  },
  {
    name: '仪表盘',
    type: 'gauge',
    optionType: 'gaugeOptions'
  }
]
</script>

<template>
  <el-row :gutter="10" class="p-10">
    <el-col v-for="{ name, optionType } in cardList" :key="name" :span="8" class="mb-10">
      <el-card :body-style="{ height: '210px' }">
        <template #header>
          <div class="card-header">
            <span>{{ name }}</span>
          </div>
        </template>
        <chart :option="store[optionType]" />
      </el-card>
    </el-col>
  </el-row>
</template>

<style scoped>
.mb-10 {
  margin-bottom: 10px;
}

.card-header {
  display: flex;
  justify-content: space-between;
}
</style>
