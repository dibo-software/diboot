<script setup lang="ts">
import { Setting, RefreshLeft, CopyDocument } from '@element-plus/icons-vue'
import useClipboard from 'vue-clipboard3'
import useAppStore from '@/store/app'
import { colorPrimary } from '@/utils/theme'

const openSetting = ref(false)

const appStore = useAppStore()

// 监听主题色
watch(
  () => appStore.colorPrimary,
  value => (colorPrimary.value = value ? value : '#409eff')
)

const clipboard = useClipboard()

const copyConfig = () => {
  let configJsonStr = JSON.stringify(appStore, ['layout', 'globalSize', 'enableTabs', 'colorPrimary'], 2)
    .replace(/"/g, "'")
    .replace(/'(.+)': /g, '$1: ')
  if (appStore.colorPrimary === undefined) {
    configJsonStr = configJsonStr.replace(/\n}/m, ',\n  colorPrimary: undefined\n}')
  }
  clipboard
    .toClipboard(configJsonStr)
    .then(() => ElMessage.success('复制成功'))
    .catch(() => ElMessage.error('写入剪切板失败，请手动配置'))
}

// 按钮拖拽
const startclientX = ref(0)
const startclientY = ref(0)
const elRight = ref(0)
const elBottom = ref(200)
const dragstart = (e: DragEvent) => {
  startclientX.value = e.clientX
  startclientY.value = e.clientY
}
const dragend = (e: DragEvent) => {
  const x = startclientX.value - e.clientX
  const y = startclientY.value - e.clientY
  elRight.value += x
  elBottom.value += y
}
</script>

<template>
  <div
    class="open-button"
    draggable="true"
    :style="`right:${elRight}px;bottom:${elBottom}px`"
    @click="openSetting = true"
    @dragstart="dragstart"
    @dragend="dragend"
  >
    <el-icon :size="22">
      <setting />
    </el-icon>
  </div>
  <div class="setting">
    <el-drawer v-model="openSetting" :title="$t('layout.setting.title')" :size="360">
      <el-scrollbar max-height="100%">
        <el-alert
          :title="$t('layout.setting.alertTitle')"
          :description="$t('layout.setting.alertDescription')"
          type="warning"
          :closable="false"
        />
        <el-divider />
        <el-form :model="appStore">
          <el-form-item :label="$t('layout.setting.layout')">
            <el-select v-model="appStore.layout">
              <el-option :label="$t('layout.setting.layoutOptions.default')" value="default" />
              <el-option :label="$t('layout.setting.layoutOptions.dock')" value="dock" />
              <el-option :label="$t('layout.setting.layoutOptions.menu')" value="menu" />
              <el-option :label="$t('layout.setting.layoutOptions.topNav')" value="topNav" />
            </el-select>
          </el-form-item>
          <el-form-item :label="$t('layout.setting.enableTabs')">
            <el-switch v-model="appStore.enableTabs" />
          </el-form-item>
          <el-form-item :label="$t('layout.setting.colorPrimary')">
            <el-color-picker v-model="colorPrimary" @update:model-value="appStore.colorPrimary = $event as string" />
          </el-form-item>
          <el-form-item :label="$t('layout.setting.enableWatermark')">
            <el-switch v-model="appStore.enableWatermark" />
          </el-form-item>
        </el-form>
      </el-scrollbar>
      <template #footer>
        <el-space direction="vertical" style="align-items: stretch; width: 100%; margin-bottom: -10px">
          <el-button type="primary" :icon="CopyDocument" @click="copyConfig">
            {{ $t('layout.setting.copyConfig') }}
          </el-button>
          <el-button text bg :icon="RefreshLeft" @click="appStore.$reset()">
            {{ $t('layout.setting.reset') }}
          </el-button>
        </el-space>
      </template>
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
