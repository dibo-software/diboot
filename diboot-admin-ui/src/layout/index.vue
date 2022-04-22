<script setup lang="ts">
import AppHeader from './header/index.vue'
import AppMenu from '@/layout/memu/index.vue'
import AppMain from './main/index.vue'
import AppFooter from './footer/index.vue'
</script>

<template>
  <el-container>
    <el-header>
      <app-header />
    </el-header>
    <el-container>
      <el-aside width="200px">
        <app-menu />
      </el-aside>
      <el-container>
        <el-main>
          <app-main>
            <template #footer>
              <app-footer />
            </template>
          </app-main>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<style scoped></style>
