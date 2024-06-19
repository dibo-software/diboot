<script setup lang="ts">
import { UserFilled } from '@element-plus/icons-vue'

const props = defineProps<{
  role: string
  message: string
}>()

const classNames = computed(() => ['chat-item', `chat-item-${props.role}`].join(' '))
</script>

<template>
  <div :class="classNames">
    <div v-if="role === 'user'" class="chat-icon">
      <el-icon><UserFilled /></el-icon>
    </div>
    <div v-else class="chat-icon">AI</div>
    <div class="chat-message" v-html="message" />
  </div>
</template>

<style scoped lang="scss">
.chat-item {
  display: flex;
  margin: 20px 20px 10px;
  border-radius: 5px;
  padding: 5px;
  line-height: 30px;
  color: var(--el-text-color);
  font-size: 15px;
  position: relative;
  .chat-icon {
    display: flex;
    box-sizing: border-box;
    width: 30px;
    height: 30px;
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
    border-radius: 50%;
    justify-content: center;
    align-items: center;
    padding: 22px;
  }
  .chat-message {
    background-color: var(--el-color-info-light-9);
    margin: 0 10px;
    padding: 0 20px;
    box-sizing: border-box;
    border-radius: 10px;
    overflow: auto;
  }
  &-user {
    .chat-icon {
      background-color: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }
    .chat-message {
      padding: 10px 20px;
      display: flex;
      align-items: center;
    }
  }
}
</style>
