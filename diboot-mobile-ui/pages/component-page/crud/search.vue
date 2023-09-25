<template>
  <u-popup width="100%" v-model="showSearch" mode="right" border-radius="14" closeable @close="close">
    <view class="u-p-t-55" style="min-height: 100%;">
      <view class="page-card u-p-l-24 u-p-r-24 u-p-b-24">
        <u-form :model="modelValue" ref="uForm" :label-width="150">
          <u-form-item label="姓名" prop="name">
            <u-input v-model="modelValue.name" placeholder="请输入姓名"/>
          </u-form-item>
          <u-form-item label="性别" prop="sex">
            <di-select v-model="modelValue.sex" placeholder="请选择性别" :list="list"></di-select>
          </u-form-item>
          <u-form-item label="水果" prop="fruits">
            <di-checkbox-list v-model="modelValue.fruits" :list="checkboxList"></di-checkbox-list>
          </u-form-item>
          <u-form-item label="味道" prop="taste">
            <di-radio-list v-model="modelValue.taste" :list="radioList"/>
          </u-form-item>
          <u-form-item label="开关">
            <template slot="right">
              <di-switch v-model="modelValue.switchVal"></di-switch>
            </template>
          </u-form-item>
          <u-form-item label="日历" prop="calendarDate">
            <di-calendar-picker v-model="modelValue.calendarDate" placeholder="请选择日期范围"/>
          </u-form-item>
          <u-form-item label="日历范围" prop="calendarRange">
            <di-calendar-picker v-model="modelValue.calendarRange" mode="range" placeholder="请选择日期范围"/>
          </u-form-item>
          <u-form-item label="地区" prop="region">
            <di-region-picker v-model="modelValue.region" placeholder="请选择地区"/>
          </u-form-item>
          <u-form-item label="时间" prop="time">
            <di-date-picker v-model="modelValue.time" placeholder="请选择时间" mode="datetime"/>
          </u-form-item>
        </u-form>
        <view class="u-m-t-60 u-flex">
          <u-button @click="reset" type="primary">重置</u-button>
          <u-button @click="confirm" type="success">确定</u-button>
        </view>
      </view>
    </view>
  </u-popup>
</template>

<script>
import search from '@/mixins/search'
export default {
  mixins: [search],
  data() {
    return {
      list: [{
        value: '1',
        label: '男'
      },
        {
          value: '2',
          label: '女'
        }
      ],
      checkboxList: [
        {
          value: 'apple',
          label: '苹果'
        },
        {
          value: 'banana',
          label: '香蕉'
        },
        {
          value: 'mango',
          label: '芒果'
        }
      ],
      radioList: [{
        label: '鲜甜',
        value: '鲜甜'
      },
        {
          label: '麻辣',
          value: '麻辣'
        }
      ],
    }
  },
};
</script>
