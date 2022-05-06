<script setup lang="ts">
import { library } from './library'

const getIconMap = (key: string): any => Object.values(library[key])

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
    <icon-show :name="modelValue" />
  </el-icon>
  <el-button type="text" @click="visible = true">{{ modelValue ? '重选' : '选择' }}</el-button>
  <el-button v-show="modelValue" type="text" style="color: var(--el-color-danger)" @click="selectIcon()">
    清除
  </el-button>
  <el-dialog v-model="visible" title="图标选择器">
    <el-tabs style="margin-top: -30px">
      <el-tab-pane v-for="key in Object.keys(library)" :key="key" lazy>
        <template #label>
          {{ key }}
          <el-tag size="small" type="info">{{ Object.keys(library[key]).length }}</el-tag>
        </template>
        <el-scrollbar style="height: 60vh">
          <el-space wrap>
            <el-card
              v-for="item in getIconMap(key)"
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
  &:hover {
    .el-icon {
      color: var(--el-color-primary);
    }
  }
}
</style>
