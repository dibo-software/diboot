<script setup lang="ts">
import { RouteRecordRaw } from 'vue-router'

defineProps<{ menuList: RouteRecordRaw[] }>()

const router = useRouter()

const goto = (name: string) => {
  router.push({ name })
}
</script>

<template>
  <template v-for="(item, index) in menuList" :key="index">
    <el-sub-menu v-if="item.children?.length > 0" :index="item.path">
      <template #title>
        图标：
        <span>{{ item.meta.title }}</span>
      </template>
      <sub-menu :menu-list="item.children" />
    </el-sub-menu>
    <el-menu-item v-else :index="item.path">
      图标：
      <span>{{ item.meta.title }}</span>
    </el-menu-item>
  </template>
</template>

<style scoped></style>
