<script setup lang="ts" name="Dashboard">
import type { EChartsOption } from 'echarts'

const stackedAreaChart: EChartsOption = {
  title: {
    text: 'Stacked Area Chart'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  legend: {
    data: ['Email', 'Union Ads', 'Video Ads', 'Direct', 'Search Engine']
  },
  toolbox: {
    feature: {
      saveAsImage: {}
    }
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: [
    {
      type: 'category',
      boundaryGap: false,
      data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
    }
  ],
  yAxis: [
    {
      type: 'value'
    }
  ],
  series: [
    {
      name: 'Email',
      type: 'line',
      stack: 'Total',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [120, 132, 101, 134, 90, 230, 210],
      smooth: true
    },
    {
      name: 'Union Ads',
      type: 'line',
      stack: 'Total',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [220, 182, 191, 234, 290, 330, 310],
      smooth: true
    },
    {
      name: 'Video Ads',
      type: 'line',
      stack: 'Total',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [150, 232, 201, 154, 190, 330, 410],
      smooth: true
    },
    {
      name: 'Direct',
      type: 'line',
      stack: 'Total',
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [320, 332, 301, 334, 390, 330, 320],
      smooth: true
    },
    {
      name: 'Search Engine',
      type: 'line',
      stack: 'Total',
      label: {
        show: true,
        position: 'top'
      },
      areaStyle: {},
      emphasis: {
        focus: 'series'
      },
      data: [820, 932, 901, 934, 1290, 1330, 1320],
      smooth: true
    }
  ]
}

//

const data: number[] = reactive([])
for (let i = 0; i < 5; ++i) {
  data.push(Math.round(Math.random() * 200))
}
const dynamicChart = {
  title: {
    text: '动态数据变化'
  },
  xAxis: {
    max: 'dataMax'
  },
  yAxis: {
    type: 'category',
    data: ['A', 'B', 'C', 'D', 'E'],
    inverse: true,
    animationDuration: 300,
    animationDurationUpdate: 300,
    max: 2 // only the largest 3 bars will be displayed
  },
  series: [
    {
      realtimeSort: true,
      name: 'X',
      type: 'bar',
      data: data,
      label: {
        show: true,
        position: 'right',
        valueAnimation: true
      }
    }
  ],
  legend: {
    show: true
  },
  animationDuration: 0,
  animationDurationUpdate: 3000,
  animationEasing: 'linear',
  animationEasingUpdate: 'linear'
}
const run = () => {
  for (let i = 0; i < data.length; ++i) {
    if (Math.random() > 0.9) {
      data[i] += Math.round(Math.random() * 2000)
    } else {
      data[i] += Math.round(Math.random() * 200)
    }
  }
}

run()
setInterval(() => {
  run()
}, 3000)

// 右

const gaugeChart: EChartsOption = {
  tooltip: {
    trigger: 'axis'
  },
  legend: {},
  toolbox: {
    show: true,
    feature: {
      saveAsImage: {}
    }
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  },
  yAxis: {
    type: 'value',
    axisLabel: {
      formatter: '{value} °C'
    }
  },
  series: [
    {
      name: 'Highest',
      type: 'line',
      data: [10, 11, 13, 11, 12, 12, 9],
      markPoint: {
        data: [
          { type: 'max', name: 'Max' },
          { type: 'min', name: 'Min' }
        ]
      },
      markLine: {
        data: [{ type: 'average', name: 'Avg' }]
      }
    },
    {
      name: 'Lowest',
      type: 'line',
      data: [1, -2, 2, 5, 3, 2, 0],
      markPoint: {
        data: [{ name: '周最低', value: -2, xAxis: 1, yAxis: -1.5 }]
      },
      markLine: {
        data: [
          { type: 'average', name: 'Avg' },
          [
            {
              symbol: 'none',
              x: '90%',
              yAxis: 'max'
            },
            {
              symbol: 'circle',
              label: {
                position: 'start',
                formatter: 'Max'
              },
              type: 'max',
              name: '最高点'
            }
          ]
        ]
      }
    }
  ]
}
//
const pieChart: EChartsOption = {
  legend: {
    top: 'bottom'
  },
  toolbox: {
    show: true,
    feature: {
      saveAsImage: { show: true }
    }
  },
  series: [
    {
      name: 'Nightingale Chart',
      type: 'pie',
      radius: [30, 100],
      center: ['50%', '50%'],
      roseType: 'area',
      itemStyle: {
        borderRadius: 8
      },
      data: [
        { value: 40, name: 'rose 1' },
        { value: 38, name: 'rose 2' },
        { value: 32, name: 'rose 3' },
        { value: 30, name: 'rose 4' },
        { value: 28, name: 'rose 5' },
        { value: 26, name: 'rose 6' },
        { value: 22, name: 'rose 7' },
        { value: 18, name: 'rose 8' }
      ]
    }
  ]
}
</script>

<template>
  <div style="height: calc(100% - 20px); padding: 10px; display: flex">
    <div style="width: calc(100% - 400px)">
      <el-card style="height: 49%" :body-style="{ height: 'calc(100% - 30px)' }">
        <Chart :option="stackedAreaChart" />
      </el-card>
      <el-card style="height: 49%; margin-top: 10px" :body-style="{ height: 'calc(100% - 30px)' }">
        <Chart :option="dynamicChart" />
      </el-card>
    </div>
    <div style="width: 390px; margin-left: 10px">
      <el-card style="height: calc(100% - 403px)" :body-style="{ height: 'calc(100%)' }">
        <Chart :option="gaugeChart" />
      </el-card>
      <el-card style="height: 390px; margin-top: 10px" :body-style="{ height: 'calc(100% - 30px)' }">
        <Chart :option="pieChart" />
      </el-card>
    </div>
  </div>

  <!--  <div class="footer">Diboot © P01-2022</div>-->
</template>

<style scoped lang="scss">
//.footer {
//  min-height: 30px;
//  padding-top: 10px;
//  color: var(--el-color-info);
//  display: flex;
//  align-items: center;
//  justify-content: space-around;
//  border-top: 2px dashed var(--el-border-color-lighter);
//}
</style>
