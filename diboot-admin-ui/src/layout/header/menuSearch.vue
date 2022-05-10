<script setup lang="ts">
import { Search } from '@element-plus/icons-vue'
import { useVueFuse } from 'vue-fuse'
import { getMenuTree } from '@/utils/route'
import { RouteRecordRaw } from 'vue-router'

interface MenuNode {
  routeName: string
  title: string
}

function tree2List(tree: RouteRecordRaw[]): MenuNode[] {
  const list: MenuNode[] = []
  for (const node of tree) {
    const children = node.children
    if (!children || !children.length) {
      list.push({ routeName: String(node.name), title: node.meta?.title ?? '' })
    } else {
      for (const child of tree2List(children)) {
        child.title = node.meta?.title + ' - ' + child.title
        list.push(child)
      }
    }
  }
  return list
}

const { search, results } = useVueFuse(tree2List(getMenuTree()), {
  // 是否按优先级进行排序
  shouldSort: true,
  // 匹配度阈值	0.0表示完全匹配(字符和位置)；1.0将会匹配所有值
  threshold: 0.3,
  // 将被搜索的键列表。 这支持嵌套路径、加权搜索、在字符串和对象数组中搜索。
  // name：搜索的键
  // weight：对应的权重
  keys: [
    {
      name: 'title',
      weight: 0.9
    },
    {
      name: 'routeName',
      weight: 0.1
    }
  ]
})
const value = ref('')
const visible = ref(false)
watch(value, v => (search.value = v))

const router = useRouter()
const open = (name: string) => {
  router.push({ name })
  visible.value = false
  value.value = ''
}
</script>

<template>
  <span class="router-search">
    <el-icon :size="22" @click="visible = true">
      <Search />
    </el-icon>
    <el-dialog v-model="visible" :show-close="false" top="10vh">
      <template #title>
        <el-input v-model="value" placeholder="搜索菜单">
          <template #prefix>
            <el-icon :size="20">
              <Search />
            </el-icon>
          </template>
        </el-input>
      </template>
      <el-scrollbar max-height="60vh">
        <el-card v-for="item in results" :key="item.routeName" shadow="hover" @click="open(item.routeName)">
          {{ item.title }}
        </el-card>
      </el-scrollbar>
    </el-dialog>
  </span>
</template>

<style scoped lang="scss">
.router-search {
  :deep(.el-dialog) {
    .el-dialog__header {
      margin-right: 0;
    }

    .el-dialog__body {
      padding: var(--el-dialog-padding-primary) var(--el-dialog-padding-primary);

      .el-card:hover {
        color: var(--el-color-primary);
      }

      .el-card + .el-card {
        margin-top: 10px;
      }
    }
  }
}
</style>
