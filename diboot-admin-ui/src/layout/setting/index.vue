<script setup lang="ts">
import { Opportunity } from '@element-plus/icons-vue'
import useAppStore from '@/store/app'
import { useCssVar } from '@vueuse/core'

const openSetting = ref(false)

const appStore = useAppStore()

// 动态主题色
// 变量前缀
const pre = '--el-color-primary'
// 白色混合色
const mixWhite = '#ffffff'
// 黑色混合色
const mixBlack = '#000000'

const colorPrimary = useCssVar(pre)

watch(
  () => appStore.colorPrimary,
  value => (colorPrimary.value = value ? value : '#409eff'),
  { immediate: true }
)

const mix = (color1: string, color2: string, weight: number) => {
  weight = Math.max(Math.min(Number(weight), 1), 0)
  const unit = (color: string, position: number) => parseInt(color.substring(position, position + 2), 16)
  const unitColor = (position: number) => {
    const num = Math.round(unit(color1, position) * (1 - weight) + unit(color2, position) * weight)
    return ('0' + (num || 0).toString(16)).slice(-2)
  }
  return '#' + unitColor(1) + unitColor(3) + unitColor(5)
}

watch(
  colorPrimary,
  value => {
    console.log(123)
    appStore.colorPrimary = value
    for (let i = 1; i < 10; i += 1) {
      useCssVar(`${pre}-light-${i}`).value = mix(value, mixWhite, i * 0.1)
    }
    useCssVar(`${pre}-dark`).value = mix(value, mixBlack, 0.1)
  },
  { immediate: true }
)
</script>

<template>
  <div class="open-button" @click="openSetting = true">
    <el-icon :size="22">
      <opportunity />
    </el-icon>
  </div>
  <div class="setting">
    <el-drawer v-model="openSetting" title="布局实时演示" :size="360">
      <el-alert
        title="以下配置可实时预览"
        description="开发者可在 `src/store/app.ts` 中修改默认值。"
        type="warning"
        :closable="false"
      />
      <el-divider />
      <el-form :model="appStore">
        <el-form-item label="开启 Tabs">
          <el-select v-model="appStore.layout">
            <el-option label="分栏" value="default" />
            <el-option label="通栏" value="dock" />
            <el-option label="经典" value="menu" />
            <el-option label="顶导航" value="topNav" />
          </el-select>
        </el-form-item>
        <el-form-item label="开启 Tabs">
          <el-switch v-model="appStore.enableTabs" />
        </el-form-item>
        <el-form-item label="开启 Footer">
          <el-switch v-model="appStore.enableFooter" />
        </el-form-item>
        <el-form-item label="主题色">
          <el-color-picker v-model="colorPrimary" />
        </el-form-item>
      </el-form>
      <el-button style="width: 100%" @click="appStore.$reset()">重置</el-button>
    </el-drawer>
  </div>
</template>

<style scoped lang="scss">
.open-button {
  width: 39px;
  height: 39px;
  z-index: 5;
  position: fixed;
  bottom: 200px;
  right: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 5px 0 0 5px;
  background: var(--el-color-primary);
  cursor: pointer;

  .el-icon {
    color: var(--el-color-white);
  }
}

.setting {
  :deep(.el-drawer__header) {
    margin-bottom: 0;
  }

  .el-form {
    :deep(.el-form-item__content) {
      justify-content: flex-end;
    }
  }
}
</style>
