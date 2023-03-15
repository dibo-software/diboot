import { defineStore } from 'pinia'
import type { EChartsOption } from 'echarts'

export const useStore = defineStore('main', {
  state: () => {
    return {
      options: {
        bar: 'barOptions',
        line: 'lineOptions',
        pie: 'pieOptions',
        radar: 'radarOptions',
        gauge: 'gaugeOptions',
        lineBar: 'lineBarOptions'
      },
      barOptions: {
        legend: {},
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: ['苹果', '香蕉', '橘子', '葡萄', '哈密瓜']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [110, 112, 100, 120, 110],
            type: 'bar',
            name: '总量',
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            data: [88, 70, 82, 100, 90],
            type: 'bar',
            name: '销量',
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      },
      lineOptions: {
        legend: {},
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: ['一月', '二月', '三月', '四月', '五月', '六月']
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            data: [620, 732, 901, 934, 1290, 1330],
            type: 'line',
            name: '水果总销量',
            smooth: true,
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      },
      pieOptions: {
        tooltip: {
          trigger: 'item'
        },
        legend: {},
        series: [
          {
            name: '销量',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            labelLine: {
              show: false
            },
            data: [
              { value: 88, name: '苹果' },
              { value: 70, name: '香蕉' },
              { value: 82, name: '橘子' },
              { value: 100, name: '葡萄' },
              { value: 90, name: '哈密瓜' }
            ]
          }
        ]
      },
      radarOptions: {
        radar: {
          indicator: [
            { name: '苹果', max: 120 },
            { name: '香蕉', max: 120 },
            { name: '橘子', max: 120 },
            { name: '葡萄', max: 120 },
            { name: '哈密瓜', max: 120 }
          ]
        },
        tooltip: {
          trigger: 'item'
        },
        color: ['#67C23A'],
        series: [
          {
            type: 'radar',
            data: [
              {
                value: [88, 70, 82, 100, 90],
                name: '好评',
                areaStyle: {
                  color: '#67C23A'
                }
              }
            ]
          }
        ]
      },
      gaugeOptions: {
        series: [
          {
            type: 'gauge',
            startAngle: 360,
            endAngle: 0,
            progress: {
              show: true,
              roundCap: true
            },
            axisTick: {
              show: false
            },
            splitLine: {
              show: false
            },
            axisLabel: {
              show: false
            },
            pointer: {
              show: false
            },
            itemStyle: {
              color: '#409EFF'
            },
            detail: {
              valueAnimation: true,
              fontSize: 40,
              offsetCenter: [0, 0],
              color: '#409EFF'
            },
            data: [
              {
                value: 50
              }
            ]
          }
        ]
      },
      lineBarOptions: {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {},
        xAxis: [
          {
            type: 'category',
            data: ['一月', '二月', '三月', '四月', '五月', '六月']
          }
        ],
        yAxis: [
          {
            type: 'value'
          },
          {
            type: 'value',
            axisLabel: {
              formatter: '{value} %'
            }
          }
        ],
        series: [
          {
            name: '总量',
            type: 'bar',
            data: [720, 832, 1001, 954, 1390, 1390],
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '总销量',
            type: 'bar',
            data: [620, 732, 901, 934, 1290, 1330],
            itemStyle: {
              color: '#67C23A'
            }
          },
          {
            name: '销售率',
            type: 'line',
            yAxisIndex: 1,
            data: [97, 98, 90, 95, 92, 93],
            itemStyle: {
              color: '#E6A23C'
            }
          }
        ]
      }
    } as Record<string, EChartsOption>
  }
})
