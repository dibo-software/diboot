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
  <el-dialog v-model="visible" title="用户信息详情" width="65vw">
    <el-descriptions v-loading="loading" :column="3" class="margin-top el-descriptions" border>
      <el-descriptions-item label="姓名">
        {{ model.realname }}
      </el-descriptions-item>
      <el-descriptions-item label="员工编号">
        {{ model.userNum }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        <el-tag :color="model.genderLabel?.ext?.color" effect="dark" type="info">
          {{ model.genderLabel?.label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="电话">
        {{ model.mobilePhone }}
      </el-descriptions-item>
      <el-descriptions-item label="邮箱">
        {{ model.email }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :color="model.statusLabel?.ext?.color" effect="dark" type="info">
          {{ model.statusLabel?.label }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="账号状态">
        {{ model.accountStatusLabel || '-' }}
      </el-descriptions-item>
      <el-descriptions-item label="角色">
        <el-tag v-for="item in model.roleList" :key="item.id" effect="plain">
          {{ item.name }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="岗位">
        <el-tag v-for="item in model.positionList" :key="item.id" type="success" effect="plain">
          {{ item.name }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="排序号">
        {{ model.sortId }}
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        {{ model.createTime }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="visible = false">关闭</el-button>
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
