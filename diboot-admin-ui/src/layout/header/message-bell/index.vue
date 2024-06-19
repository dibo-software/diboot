<script setup lang="ts">
import { Bell } from '@element-plus/icons-vue'
import MessageList from './List.vue'

const visible = ref(false)
const activeKey = ref('unreadMessage')
const countMap: Record<string, number> = reactive({ unreadMessage: 0 })
const total = computed(() => Object.values(countMap).reduce((prev, next) => prev + next, 0))

const unreadMessage = ref()
const allMessages = ref()
</script>

<template>
  <div>
    <el-popover v-model="visible" width="auto" :show-after="200">
      <template #reference>
        <el-badge :value="total" :hidden="!total" @click="visible = true">
          <el-icon :size="22">
            <Bell />
          </el-icon>
        </el-badge>
      </template>

      <el-tabs v-model="activeKey" style="width: 360px">
        <el-tab-pane name="unreadMessage">
          <template #label>
            {{ $t('layout.messageBell.unRead') }}
            <el-badge class="custom-badge" :value="countMap.unreadMessage" :hidden="countMap.unreadMessage === 0" />
          </template>
          <message-list
            ref="unreadMessage"
            :unread="true"
            @close="visible = false"
            @reset="allMessages?.refresh()"
            @total="value => (countMap.unreadMessage = value)"
          />
        </el-tab-pane>
        <el-tab-pane name="allMessages" :label="$t('layout.messageBell.all')" lazy>
          <message-list ref="allMessages" :unread="false" @reset="unreadMessage?.refresh()" @close="visible = false" />
        </el-tab-pane>
      </el-tabs>
    </el-popover>
  </div>
</template>
