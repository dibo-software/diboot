<script setup lang="ts" name="Markdown">
const value = ref<string>('Vue Composition API + Vditor + TypeScript Minimal Example')

value.value += '$$x^2+2x+1=\\frac{1}{y^2}$$'

value.value +=
  '\n```echarts\n' +
  '{\n' +
  '  "title": { "text": "最近 30 天" },\n' +
  '  "tooltip": { "trigger": "axis", "axisPointer": { "lineStyle": { "width": 0 } } },\n' +
  '  "legend": { "data": ["帖子", "用户", "回帖"] },\n' +
  '  "xAxis": [{\n' +
  '      "type": "category",\n' +
  '      "boundaryGap": false,\n' +
  '      "data": ["2019-05-08","2019-05-09","2019-05-10","2019-05-11","2019-05-12","2019-05-13","2019-05-14","2019-05-15","2019-05-16","2019-05-17","2019-05-18","2019-05-19","2019-05-20","2019-05-21","2019-05-22","2019-05-23","2019-05-24","2019-05-25","2019-05-26","2019-05-27","2019-05-28","2019-05-29","2019-05-30","2019-05-31","2019-06-01","2019-06-02","2019-06-03","2019-06-04","2019-06-05","2019-06-06","2019-06-07"],\n' +
  '      "axisTick": { "show": false },\n' +
  '      "axisLine": { "show": false }\n' +
  '  }],\n' +
  '  "yAxis": [{ "type": "value", "axisTick": { "show": false }, "axisLine": { "show": false }, "splitLine": { "lineStyle": { "color": "rgba(0, 0, 0, .38)", "type": "dashed" } } }],\n' +
  '  "series": [\n' +
  '    {\n' +
  '      "name": "帖子", "type": "line", "smooth": true, "itemStyle": { "color": "#d23f31" }, "areaStyle": { "normal": {} }, "z": 3,\n' +
  '      "data": ["18","14","22","9","7","18","10","12","13","16","6","9","15","15","12","15","8","14","9","10","29","22","14","22","9","10","15","9","9","15","0"]\n' +
  '    },\n' +
  '    {\n' +
  '      "name": "用户", "type": "line", "smooth": true, "itemStyle": { "color": "#f1e05a" }, "areaStyle": { "normal": {} }, "z": 2,\n' +
  '      "data": ["31","33","30","23","16","29","23","37","41","29","16","13","39","23","38","136","89","35","22","50","57","47","36","59","14","23","46","44","51","43","0"]\n' +
  '    },\n' +
  '    {\n' +
  '      "name": "回帖", "type": "line", "smooth": true, "itemStyle": { "color": "#4285f4" }, "areaStyle": { "normal": {} }, "z": 1,\n' +
  '      "data": ["35","42","73","15","43","58","55","35","46","87","36","15","44","76","130","73","50","20","21","54","48","73","60","89","26","27","70","63","55","37","0"]\n' +
  '    }\n' +
  '  ]\n' +
  '}\n' +
  '\n```\n'

const isRead = ref(false)
</script>

<template>
  <div style="height: calc(100% - 20px); padding: 10px">
    <el-space style="justify-content: space-between; width: 100%; margin-bottom: 5px">
      <el-alert type="success" :closable="false">
        <span style="font-size: 13px">感谢优秀的 `Vditor`，项目地址：</span>
        <el-link type="primary" href="https://github.com/Vanessa219/vditor">
          https://github.com/Vanessa219/vditor
        </el-link>
      </el-alert>
      <el-button type="primary" @click="isRead = !isRead"> 切换为：{{ isRead ? '编辑' : '显示' }}模式 </el-button>
    </el-space>
    <el-scrollbar v-if="isRead" style="height: calc(100% - 45px)">
      <MdRead :value="value" />
    </el-scrollbar>
    <MdEditor v-else v-model="value" height="calc(100% - 45px)" />
  </div>
</template>
