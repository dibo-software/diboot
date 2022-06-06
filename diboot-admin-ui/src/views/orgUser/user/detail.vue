<script setup lang="ts" name="UserDetail">
import type { UserModel } from './type'

const { model, loadData, loading } = useDetailDefault<UserModel>('/user')

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
      <el-descriptions-item label="编号">
        {{ model.userNum }}
      </el-descriptions-item>
      <el-descriptions-item label="性别">
        {{ model.genderLabel }}
      </el-descriptions-item>
      <el-descriptions-item label="电话">
        {{ model.mobilePhone }}
      </el-descriptions-item>
      <el-descriptions-item label="邮箱">
        {{ model.email }}
      </el-descriptions-item>
      <el-descriptions-item label="状态">
        {{ model.statusLabel }}
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
