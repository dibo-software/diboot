<script setup lang="ts" name="Dashboard">
// 可接收 已使用的可视高度，方便主区域计算使用
defineProps<{ usedVisibleHeight?: number }>()

const value = ref<string>('Element:HotWater')

const roles = ['admin', 'develop', 'test']
const permissions = ['detail', 'create', 'update', 'delete', 'import', 'export']
</script>

<template>
  Dashboard
  <p>已使用的可视高度：{{ usedVisibleHeight }}px</p>

  回显图标：
  <el-icon :size="25" style="vertical-align: middle">
    <icon :name="value" />
  </el-icon>

  <br />

  图标选择：
  <icon-select v-model="value" />

  <br />
  角色：
  <el-tag v-for="item in roles" :key="item" v-has-role="[item]">{{ item }}</el-tag>
  <br />
  权限：
  <el-tag v-for="item in permissions" :key="item" v-has-permission="item">{{ item }}</el-tag>

  <!--  <div class="footer">Diboot © 2015-2022</div>-->
</template>

<style scoped lang="scss">
.footer {
  min-height: 30px;
  padding-top: 10px;
  color: var(--el-color-info);
  display: flex;
  align-items: center;
  justify-content: space-around;
  border-top: 2px dashed var(--el-border-color-lighter);
}
</style>
