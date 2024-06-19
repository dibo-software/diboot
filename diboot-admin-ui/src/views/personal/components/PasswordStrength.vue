<script setup lang="ts">
import { level } from './check-password'
import { useI18n } from 'vue-i18n'
const i18n = useI18n()
const props = defineProps<{
  password?: string
}>()

// 强度条颜色
const barColor = ref('')
// 强度条长度
const width = ref('')
// 强度条说明
const strength = ref('')

// 监听注册页面的新密码变化状态，来改变密码强弱显示
watch(
  () => props.password,
  newVal => {
    if (newVal && newVal != '') {
      const res = level(newVal)
      if (res == '弱') {
        barColor.value = 'red'
        width.value = '35'
        strength.value = i18n.t('password.weak')
      } else if (res == '一般') {
        barColor.value = 'orange'
        width.value = '65'
        strength.value = i18n.t('password.general')
      } else if (res == '强') {
        barColor.value = '#1B8EF8'
        width.value = '100'
        strength.value = i18n.t('password.strong')
      }
    }
  }
)
</script>

<template>
  <el-form ref="ruleFormRef" :label-width="$i18n.locale === 'en' ? '150px' : '120px'">
    <el-form-item v-if="password !== '' && password !== undefined" label="" align="center" style="height: 25px">
      <!-- 展示长度条 -->
      <div
        v-if="password !== '' && password !== undefined"
        class="bar"
        :style="{ background: barColor, width: width + '%' }"
      >
        <!-- 展示文字 -->
        <div v-if="password !== '' && password !== undefined" class="strength" :style="{ color: barColor }">
          {{ strength }}
        </div>
      </div>
    </el-form-item>
  </el-form>
</template>

<style scoped>
.strength {
  font-size: 13px;
  color: #271e25;
  position: relative;
  top: 5px;
  left: 0;
  transition: 0.5s all ease;
}
.bar {
  /* width: 400px; */
  height: 5px;
  background: red;
  transition: 0.5s all ease;
  /*max-width: 420px;*/
  /*margin: 2px 0 5px 5px;*/
  position: absolute;
}
</style>
