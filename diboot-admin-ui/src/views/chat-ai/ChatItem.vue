<script setup lang="ts">
import { UserFilled, ArrowDown, ArrowUp } from '@element-plus/icons-vue'

const props = defineProps<{
  role: string
  message: string
  reasoning?: string
}>()

const classNames = computed(() => ['chat-item', `chat-item-${props.role}`].join(' '))

const collapse = ref(true)
</script>

<template>
  <div :class="classNames">
    <div v-if="role === 'user'" class="chat-icon">
      <el-icon><UserFilled /></el-icon>
    </div>
    <div v-else class="chat-icon">AI</div>
    <div class="chat-message">
      <div v-if="reasoning" class="reasoning">
        <el-button round @click="collapse = !collapse">
          已深度思考
          <el-icon v-if="collapse"><ArrowUp /></el-icon>
          <el-icon v-else><ArrowDown /></el-icon>
        </el-button>
        <div v-if="reasoning && collapse" class="content">
          <div class="line" />
          <div v-text="reasoning" />
        </div>
      </div>
      <div v-html="message" />
    </div>
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

    .reasoning {
      margin: 20px 0;

      .content {
        color: #8b8b8b;
        white-space: pre-wrap;
        padding: 0 0 0 13px;
        line-height: 26px;
        position: relative;
        font-size: 13px;

        .line {
          border-left: 2px solid #e5e5e5;
          height: calc(100% - 10px);
          margin-top: 5px;
          position: absolute;
          left: 0;
          top: 0;
        }
      }
    }
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
