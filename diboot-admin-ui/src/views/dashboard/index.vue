<script setup lang="ts" name="Dashboard">
import type { EChartsOption } from 'echarts'
import logoSrc from '@/assets/logo.png'

const tags: Array<{ label: string; type: '' | 'success' | 'info' | 'warning' | 'danger' }> = [
  {
    label: '低代码',
    type: ''
  },
  {
    label: '代码生成器',
    type: 'success'
  },
  {
    label: 'vue',
    type: 'info'
  },
  {
    label: 'flowable',
    type: 'warning'
  },
  {
    label: '工作流',
    type: 'danger'
  }
]

const progress = [
  {
    label: 'Java',
    value: 62.1
  },
  {
    label: 'Vue',
    value: 27.9
  },
  {
    label: 'JavaScript',
    value: 6.2
  },
  {
    label: 'CSS',
    value: 3.2
  },
  {
    label: 'SCSS',
    value: 0.6
  }
]

const radarChart: EChartsOption = {
  radar: {
    indicator: [
      { name: '代码活跃度', max: 50 },
      { name: '社区活跃度', max: 50 },
      { name: '团队健康', max: 50 },
      { name: '流行趋势', max: 50 },
      { name: '影响力', max: 50 }
    ],
    radius: 90
  },
  series: [
    {
      type: 'radar',
      areaStyle: {},
      data: [
        {
          value: [27, 10, 44, 38, 14]
        }
      ]
    }
  ]
}

const currentDate = ref(new Date())
</script>

<template>
  <div class="p-10 fs-dynamic">
    <el-row :gutter="10">
      <el-col :lg="10" :md="24" :xs="24">
        <el-card class="h100" shadow="hover">
          <div class="fw-bold mb-20" style="font-size: calc(var(--el-font-size-dynamic) + 4px)">欢迎</div>
          <div class="flex flex-dir-col flex-col-center">
            <el-image class="mb-20" style="width: 100px; height: 100px" :src="logoSrc" />
            <span class="mb-20 fw-bold" style="font-size: calc(var(--el-font-size-dynamic) + 14px)">
              欢迎体验 diboot低代码开发平台
            </span>
            <span class="mb-20">
              写的更少, 性能更好 ->
              为开发人员打造的低代码开发平台。Mybatis-plus关联查询，关联无SQL，性能高10倍，前后端代码可视化生成，flowable工作流，spring
              cloud微服务等全方位赋能！
            </span>
            <div class="mb-20" style="align-self: flex-start">
              <el-tag
                v-for="({ label, type }, index) in tags"
                :key="label"
                :class="{ 'ml-10': index !== 0 }"
                :type="type"
              >
                {{ label }}
              </el-tag>
            </div>
            <span style="align-self: flex-end">
              官网链接：<a class="main-color" target="_blank" href="https://www.diboot.com">https://www.diboot.com</a>
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :lg="7" :md="12" :xs="24" style="display: flex" class="flex-dir-col">
        <el-card class="mb-10" shadow="hover">
          <div class="fw-bold mb-20" style="font-size: calc(var(--el-font-size-dynamic) + 4px)">关于项目</div>
          <div class="main-color mb-10 fw-bold" style="font-size: calc(var(--el-font-size-dynamic) + 2px)">
            基础组件化繁为简，高效工具以简驭繁
          </div>
          <div class="mb-20">基于Vue3 + Element-Plus 的中后台前端解决方案，如果喜欢就点个星星支持一下。</div>
          <a class="gitee" href="https://gitee.com/dibo_software/diboot" target="_blank">
            <img src="http://gitee.com/dibo_software/diboot/badge/star.svg?theme=dark" />
          </a>
        </el-card>
        <el-card style="flex: 1" shadow="hover">
          <div class="fw-bold mb-20" style="font-size: calc(var(--el-font-size-dynamic) + 4px)">活跃度</div>
          <Chart :option="radarChart" style="height: 300px" />
        </el-card>
      </el-col>
      <el-col :lg="7" :md="12" :xs="24" style="display: flex" class="flex-dir-col">
        <el-card class="mb-10" shadow="hover">
          <el-calendar v-model="currentDate">
            <template #header="{ date }">
              <span>{{ date }}</span>
            </template>
          </el-calendar>
        </el-card>
        <el-card shadow="hover" style="flex: 1">
          <div class="fw-bold mb-20" style="font-size: calc(var(--el-font-size-dynamic) + 4px)">语言</div>
          <div
            v-for="{ label, value } in progress"
            :key="label"
            style="display: flex; align-items: center"
            class="mb-10"
          >
            <span style="width: 80px; text-align: right">{{ label }}</span>
            <el-progress :percentage="value" :stroke-width="10" style="flex: 1" class="ml-10" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.el-calendar {
  :deep(.el-calendar-day) {
    height: unset;
  }
}

.gitee {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 90px;
  color: #e7e7e7;
  background-color: #393939;
  border-radius: 3px;
}
</style>
