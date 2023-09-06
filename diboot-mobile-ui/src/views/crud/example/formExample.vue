<script setup>
import { areaList } from '@vant/area-data'

const name = ref('帝博软件')
const type = ref('1')
const contacts = ref('张三')
const telephone = ref('')
const department = ref(['1'])
const isCar = ref(false)
const carCode = ref('')
const annex = ref([])

const city = ref('')
const showArea = ref(false)
const onCityConfirm = ({ selectedOptions }) => {
  showArea.value = false
  city.value = selectedOptions.map((item) => item.text).join('/')
}

const industry = ref('')
const showPicker = ref(false)
const columns = [
  { text: '制造业', value: 'ZZY' },
  { text: '信息技术', value: 'IT' },
  { text: '农业', value: 'NY' },
  { text: '金融', value: 'JR' },
  { text: '教育', value: 'EDU' },
  { text: '环保', value: 'HB' },
  { text: '医疗', value: 'YL' },
  { text: '其他', value: 'QT' },
  { text: '未知', value: 'WZ' }
]
const onIndustryConfirm = ({ selectedOptions }) => {
  industry.value = selectedOptions[0]?.text
  showPicker.value = false
}

const date = ref('')
const showDate = ref(false)
const onDateConfirm = ({ selectedValues }) => {
  date.value = selectedValues.join('/')
  showDate.value = false
}

const onSubmit = (values) => {
  console.log('submit', values)
}
</script>

<template>
  <van-form @submit='onSubmit'>
    <van-field
      v-model='name'
      name='客户名'
      label='客户名'
      placeholder='客户名'
      :rules="[{ required: true, message: '请填写客户名' }]"
    />
    <van-field name='radio' label='客户类型'>
      <template #input>
        <van-radio-group v-model='type' direction='horizontal'>
          <van-radio name='1'>企业客户</van-radio>
          <van-radio name='2'>个人客户</van-radio>
        </van-radio-group>
      </template>
    </van-field>
    <van-field
      v-model='contacts'
      name='联系人'
      label='联系人'
      placeholder='联系人'
      :rules="[{ required: true, message: '请填写联系人' }]"
    />
    <van-field
      v-model='telephone'
      name='联系电话'
      label='联系电话'
      placeholder='联系电话'
      :rules="[{ required: true, message: '请填写联系电话' }]"
    />
    <van-field
      v-model='city'
      is-link
      readonly
      name='area'
      label='所在城市'
      placeholder='点击选择所在城市'
      @click='showArea = true'
    />
    <van-popup v-model:show='showArea' position='bottom'>
      <van-area
        :area-list='areaList'
        @confirm='onCityConfirm'
        @cancel='showArea = false'
      />
    </van-popup>
    <van-field
      v-model='industry'
      is-link
      readonly
      name='picker'
      label='所属行业'
      placeholder='点击选择所属行业'
      @click='showPicker = true'
    />
    <van-popup v-model:show='showPicker' position='bottom'>
      <van-picker
        :columns='columns'
        @confirm='onIndustryConfirm'
        @cancel='showPicker = false'
      />
    </van-popup>
    <van-field
      v-model='date'
      is-link
      readonly
      name='datePicker'
      label='来访日期'
      placeholder='点击选择来访日期'
      @click='showDate = true'
    />
    <van-popup v-model:show='showDate' position='bottom'>
      <van-date-picker @confirm='onDateConfirm' @cancel='showDate = false' />
    </van-popup>
    <van-field name='checkboxGroup' label='拜访部门'>
      <template #input>
        <van-checkbox-group v-model='department' direction='horizontal'>
          <van-checkbox name='1' shape='square'>总裁办</van-checkbox>
          <van-checkbox name='2' shape='square'>财务部</van-checkbox>
          <van-checkbox name='3' shape='square'>开发部</van-checkbox>
        </van-checkbox-group>
      </template>
    </van-field>
    <van-field name='switch' label='是否开车'>
      <template #input>
        <van-switch v-model='isCar' />
      </template>
    </van-field>
    <van-field
      v-if='isCar'
      v-model='carCode'
      name='车牌号'
      label='车牌号'
      placeholder='车牌号'
      :rules="[{ required: true, message: '请填写车牌号' }]"
    />
    <van-field name='uploader' label='其他资料'>
      <template #input>
        <van-uploader v-model='annex' upload-icon='plus' upload-text='上传' />
      </template>
    </van-field>
    <div style='margin: 16px;'>
      <van-button round block type='primary' native-type='submit'>
        提交
      </van-button>
    </div>
  </van-form>

</template>

<style scoped>

</style>
