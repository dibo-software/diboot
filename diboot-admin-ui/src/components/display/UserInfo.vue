<script setup lang="ts">
import { ClickOutside as vClickOutside } from 'element-plus'

import type { PopoverInstance } from 'element-plus'
import { buildImgSrc } from '@/utils/file'
import type { UserModel } from '@/views/org-structure/user/type'

interface UserInfoProps {
  id: string
  name: string
  avatar?: string
  gender?: string
}
const props = withDefaults(defineProps<UserInfoProps>(), {
  avatar: ''
})
// 头像背景色
const avatarBackgroundColor = computed(() => {
  if (props.gender === 'F') return '#FD8BB8'
  else if (props.gender === 'M') return '#55B0EE'
  else return ''
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
const onClickOutside = () => {
  model.value = {}
  popoverRef.value?.hide()
}

const { model, loadData, loading } = useDetail<UserModel>('/iam/user')
</script>

<template>
  <div style="display: flex">
    <div ref="buttonRef" v-click-outside="onClickOutside" class="user-info">
      <div v-if="avatar" class="avatar">
        <img :src="buildImgSrc(avatar)" />
      </div>
      <div v-else class="avatar placeholder" :style="{ backgroundColor: avatarBackgroundColor }">
        {{ avatarText }}
      </div>
      <span class="name">{{ name }}</span>
    </div>
  </div>
  <el-popover
    ref="popoverRef"
    :virtual-ref="buttonRef"
    trigger="click"
    virtual-triggering
    width="300px"
    @show="loadData(id)"
  >
    <el-descriptions v-loading="loading" size="small" direction="horizontal" :column="1" border>
      <el-descriptions-item :label="$t('user.userNum')"> {{ model.userNum }}</el-descriptions-item>
      <el-descriptions-item :label="$t('user.role')">
        <el-tag v-for="item in model.roleList" :key="item.id" effect="plain">
          {{ item.name }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item v-if="model?.userPositionList" :label="$t('position.label')">
        <template v-if="model.userPositionList?.length === 1">
          <el-tag v-for="item in model.userPositionList" :key="item.id" type="success" effect="plain">
            {{ item.positionName }}
          </el-tag>
        </template>
        <template v-else-if="model.userPositionList?.length ?? 0 > 1">
          <span style="font-size: 12px">{{ $t('position.main') }}：</span>
          <template v-for="item in model.userPositionList" :key="item.id">
            <el-tag v-if="item.isPrimaryPosition" type="success" effect="plain">
              {{ item.positionName }}
            </el-tag>
          </template>
          <br />
          <span style="font-size: 12px">{{ $t('position.deputy') }}：</span>
          <template v-for="item in model.userPositionList" :key="item.id">
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
  border-radius: 50%;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.avatar img {
  height: 100%;
  width: 100%;
  object-fit: cover;
}

.avatar.placeholder {
  border: none;
  padding: 5px;
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
