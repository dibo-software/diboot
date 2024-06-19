<script setup lang="ts">
import IconLibrary from '@/config/icon-library'

const getIconList = (key: string) =>
  Object.values(IconLibrary[key] as Record<string, { name: string } & Record<string, unknown>>)

defineProps<{ modelValue?: string }>()

const emits = defineEmits<{
  (e: 'update:modelValue', value?: string): void
}>()

const visible = ref(false)

const selectIcon = (name?: string) => {
  emits('update:modelValue', name)
  visible.value = false
}
</script>
<template>
  <el-icon :size="25" style="vertical-align: middle; margin-right: 10px">
    <icon :name="modelValue" />
  </el-icon>
  <el-button text bg type="primary" @click="visible = true">
    {{ modelValue ? $t('components.icon.reelect') : $t('components.icon.choose') }}</el-button
  >
  <el-button v-show="modelValue" text bg type="danger" @click="selectIcon()">
    {{ $t('components.icon.clear') }}
  </el-button>
  <el-dialog v-model="visible" :title="$t('components.icon.selector')" top="10vh">
    <el-tabs style="margin-top: -30px">
      <el-tab-pane v-for="key in Object.keys(IconLibrary)" :key="key" lazy>
        <template #label>
          {{ key }}
          <el-tag size="small" type="info">{{ Object.keys(IconLibrary[key]).length }}</el-tag>
        </template>
        <el-scrollbar height="60vh">
          <el-space wrap>
            <el-card
              v-for="item in getIconList(key)"
              :key="item.name"
              shadow="hover"
              @click="selectIcon(`${key}:${item.name}`)"
            >
              <el-tooltip :content="item.name" :show-after="200">
                <el-icon :size="28">
                  <component :is="item" />
                </el-icon>
              </el-tooltip>
            </el-card>
          </el-space>
        </el-scrollbar>
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-card {
  cursor: pointer;

  &:hover .el-icon {
    color: var(--el-color-primary);
  }
}
</style>
