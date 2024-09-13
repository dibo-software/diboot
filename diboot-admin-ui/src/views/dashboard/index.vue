<script setup lang="ts" name="Dashboard">
import type { EChartsOption } from 'echarts'
import logoSrc from '@/assets/logo.png'
import { StarFilled } from '@element-plus/icons-vue'

const tags: Array<{ label: string; type?: 'success' | 'info' | 'warning' | 'danger' }> = [
  {
    label: '低代码',
    type: 'success'
  },
  {
    label: '代码生成器',
    type: 'success'
  },
  {
    label: '零代码',
    type: void 0
  },
  {
    label: '表单设计器',
    type: void 0
  },
  {
    label: 'Flowable工作流',
    type: 'warning'
  },
  {
    label: '流程设计器',
    type: 'warning'
  },
  {
    label: 'AI',
    type: void 0
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
      { name: '代码活跃度', max: 100 },
      { name: '社区活跃度', max: 100 },
      { name: '团队健康', max: 100 },
      { name: '流行趋势', max: 100 },
      { name: '影响力', max: 100 }
    ],
    radius: 90
  },
  series: [
    {
      type: 'radar',
      areaStyle: {},
      data: [
        {
          value: [80, 75, 80, 80, 78]
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
              欢迎使用 Diboot低代码开发平台
            </span>
            <span class="mb-20">
              写的更少, 性能更好 -> 为开发人员打造的低代码开发平台，生于开发框架，无扩展局限，零代码/低代码/全代码
              顺畅融合自由切换。
              Mybatis-plus关联查询，关联无SQL，性能高10倍，前后端代码可视化生成，Flowable工作流，Spring
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
          <div class="fw-bold mb-20" style="font-size: calc(var(--el-font-size-dynamic) + 4px)">Diboot</div>
          <div class="mb-10 fw-bold" style="font-size: calc(var(--el-font-size-dynamic) + 2px)">
            基础组件化繁为简，高效工具以简驭繁
          </div>
          <div class="mb-20">
            感谢您使用Diboot，喜欢就帮我们点个 <el-icon color="red"><StarFilled /></el-icon> 鼓励一下，谢谢
          </div>
          <el-row>
            <el-col :span="8">
              <a
                href="http://gitee.com/dibo_software/diboot"
                target="_blank"
                title="Gitee 仓库 - 开源不易，star鼓励"
                style="margin-right: 20px"
              >
                <img src="http://gitee.com/dibo_software/diboot/badge/star.svg?theme=dark" />
              </a>
            </el-col>
            <el-col :span="8">
              <a href="http://github.com/dibo-software/diboot" target="_blank" title="Github 仓库 - 开源不易，star鼓励">
                <img src="https://img.shields.io/github/stars/dibo-software/diboot.svg?style=social&label=Stars" />
              </a>
            </el-col>
            <el-col :span="8">
              <el-popover
                placement="bottom-end"
                title="感谢每一份信任与支持"
                :width="200"
                trigger="hover"
                style="margin-right: 10px"
              >
                <el-image src="https://www.diboot.com/wechat_donate.png" fit="contain" />
                <template #reference>
                  <el-button type="success" link>捐助支持 Diboot</el-button>
                </template>
              </el-popover>
            </el-col>
          </el-row>
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
