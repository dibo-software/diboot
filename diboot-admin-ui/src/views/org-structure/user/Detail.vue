<script setup lang="ts">
import type { UserModel } from './type'

const { model, loadData, loading } = useDetail<UserModel>('/iam/user')

const visible = ref(false)

defineExpose({
  open: (id: string) => {
    loadData(id)
    visible.value = true
  }
})
</script>

<template>
  <el-dialog v-model="visible" :title="$t('title.detail')" width="65vw">
    <el-descriptions v-loading="loading" :column="3" class="margin-top el-descriptions" border>
      <el-descriptions-item :label="$t('user.userNum')">
        {{ model.userNum }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.realname')">
        {{ model.realname }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.gender')">
        <el-tag :color="model.genderLabel?.ext?.color" effect="dark" type="info">
          {{ model.genderLabel?.label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.mobilePhone')">
        {{ model.mobilePhone }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.email')">
        {{ model.email }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.status')">
        <el-tag :color="model.statusLabel?.ext?.color" effect="dark" type="info">
          {{ model.statusLabel?.label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.accountStatus')">
        {{ model.accountStatusLabel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.role')">
        <el-tag v-for="item in model.roleList" :key="item.id" effect="plain">
          {{ item.name }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('position.label')">
        <el-tag v-for="item in model.positionList" :key="item.id" type="success" effect="plain">
          {{ item.name }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item :label="$t('user.sortId')">
        {{ model.sortId }}
      </el-descriptions-item>
      <el-descriptions-item :label="$t('baseField.createTime')">
        {{ model.createTime }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">{{ $t('button.close') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.el-descriptions {
  :deep(.el-descriptions__label) {
    width: 100px;
  }
}
</style>
