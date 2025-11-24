<script setup lang="ts">
import { ClickOutside as vClickOutside } from 'element-plus'

import type { PopoverInstance } from 'element-plus'
import type { Role } from '@/views/system/role/type'
import type { Position } from '@/views/org-structure/position/type'

interface UserInfoProps {
  name: string
  avatar?: string
  popover?: {
    userNum: string
    roleList?: Role[]
    positionList?: Position[]
  }
}
const props = withDefaults(defineProps<UserInfoProps>(), {
  avatar: ''
})

// 随机生成头像背景色
const avatarBackgroundColor = computed(() => {
  if (props.avatar) return ''
  const colors = ['#409EFF', '#67C23A', '#E6A23C', '#F56C6C', '#34C759']
  return colors[Math.floor(Math.random() * colors.length)]
})

/**
 * 如果props.name是中文，多余两位去后两位，小于取全部，英文取首字母
 */
const avatarText = computed(() => {
  if (!props.name) return ''

  const hasChinese = /[\u4e00-\u9fa5]/.test(props.name)

  if (hasChinese) return props.name.slice(-2)
  else return props.name.charAt(0).toUpperCase()
})

const buttonRef = ref()
const popoverRef = ref<PopoverInstance>()
const onClickOutside = () => popoverRef.value?.hide()
</script>

<template>
  <div style="display: flex">
    <div ref="buttonRef" v-click-outside="onClickOutside" class="user-info">
      <div v-if="avatar" class="avatar">
        <img :src="avatar" :alt="name" />
      </div>
      <div v-else class="avatar placeholder" :style="{ backgroundColor: avatarBackgroundColor }">
        {{ avatarText }}
      </div>
      <span class="name">{{ name }}</span>
    </div>
  </div>
  <el-popover v-if="popover" ref="popoverRef" :virtual-ref="buttonRef" trigger="hover" virtual-triggering width="300px">
    <el-descriptions size="small" direction="horizontal" :column="1" border>
      <el-descriptions-item :label="$t('user.userNum')"> {{ popover.userNum }}</el-descriptions-item>
      <el-descriptions-item :label="$t('user.role')">
        <el-tag v-for="item in popover.roleList" :key="item.id" effect="plain">
          {{ item.name }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('position.label')">
        <template v-if="popover.userPositionList?.length === 1">
          <el-tag v-for="item in popover.userPositionList" :key="item.id" type="success" effect="plain">
            {{ item.positionName }}
          </el-tag>
        </template>
        <template v-else-if="popover.userPositionList?.length ?? 0 > 1">
          <span style="font-size: 12px">{{ $t('position.main') }}：</span>
          <template v-for="item in popover.userPositionList" :key="item.id">
            <el-tag v-if="item.isPrimaryPosition" type="success" effect="plain">
              {{ item.positionName }}
            </el-tag>
          </template>
          <br />
          <span style="font-size: 12px">{{ $t('position.deputy') }}：</span>
          <template v-for="item in popover.userPositionList" :key="item.id">
            <el-tag v-if="!item.isPrimaryPosition" type="warning" effect="plain">
              {{ item.positionName }} ({{ item.orgName }})
            </el-tag>
          </template>
        </template>
      </el-descriptions-item>
    </el-descriptions>
  </el-popover>
</template>

<style scoped lang="scss">
.user-info {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  padding: 2px 3px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  &:hover {
    background-color: var(--el-color-info-light-7);
  }
}

.avatar {
  min-width: 28px;
  width: 28px;
  height: 28px;
  padding: 5px;
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar.placeholder {
  border: none;
}

.name {
  margin-left: 5px;
}

.el-descriptions {
  :deep(.el-descriptions__label) {
    width: 100px;
  }
}
</style>
