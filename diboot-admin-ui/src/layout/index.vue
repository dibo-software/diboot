<script setup lang="ts">
import AppHeader from './header/index.vue'
import AppMenu from './memu/index.vue'
import AppMain from './main/index.vue'
import AppFooter from './footer/index.vue'
import AppTabs from './tabs/index.vue'
</script>

<template>
  <el-container>
    <el-header height="50px" style="border-bottom: 1px solid #eee">
      <app-header />
    </el-header>
    <el-container>
      <el-aside width="200px">
        <el-scrollbar height="calc(100vh - 50px)">
          <app-menu />
        </el-scrollbar>
      </el-aside>
      <el-container>
        <el-main style="padding: 0">
          <app-tabs />
          <el-scrollbar style="height: calc(100vh - 86px)">
            <app-main>
              <template #footer>
                <app-footer />
              </template>
            </app-main>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </el-container>
</template>

<style scoped></style>
