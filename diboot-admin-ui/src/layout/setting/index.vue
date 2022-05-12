<script setup lang="ts">
import { Opportunity } from '@element-plus/icons-vue'
import useAppStore from '@/store/app'
import { colorPrimary } from '@/utils/theme'

const openSetting = ref(false)

const appStore = useAppStore()

// 监听主题色
watch(
  () => appStore.colorPrimary,
  value => (colorPrimary.value = value ? value : '#409eff')
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
          <el-color-picker v-model="colorPrimary" @update:model-value="value => (appStore.colorPrimary = value)" />
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
