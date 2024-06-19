<script setup lang="ts">
import type { RouteRecordRaw } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { useVueFuse } from 'vue-fuse'
import { getMenuTree } from '@/utils/route'

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
const inputRef = ref()

const open = () => {
  visible.value = true
  setTimeout(() => {
    inputRef.value?.focus()
  }, 0)
}

// 关闭搜索菜单后清空搜索列表
watch(visible, val => {
  !val && results.value.splice(0)
})

// 监听全局快捷键 （ctrl + k）
onMounted(() => {
  let keyList: string[] = []
  document.addEventListener('keyup', e => {
    keyList = keyList.filter(key => key !== e.key)
  })
  document.addEventListener('keydown', e => {
    keyList.push(e.key)
    if (keyList[0] === 'Control' && keyList[1] === 'k') {
      e.preventDefault()
      open()
    }
  })
})

const router = useRouter()
const go = (name: string) => {
  router.push({ name })
  visible.value = false
  value.value = ''
}
</script>

<template>
  <span class="menu-search">
    <el-tooltip effect="dark" :content="$t('layout.header.menuSearch')" placement="bottom" :show-after="300">
      <el-icon :size="22" @click="open">
        <Search />
      </el-icon>
    </el-tooltip>
    <el-dialog v-model="visible" :show-close="false" top="10vh">
      <template #header>
        <el-input
          ref="inputRef"
          v-model="value"
          autofocus
          :placeholder="$t('layout.header.menuSearch')"
          @update:model-value="(v: string) => (search = v)"
        >
          <template #prefix>
            <el-icon :size="20">
              <Search />
            </el-icon>
          </template>
        </el-input>
      </template>
      <el-scrollbar max-height="60vh">
        <el-card v-for="item in results" :key="item.routeName" shadow="hover" @click="go(item.routeName)">
          {{ item.title }}
        </el-card>
      </el-scrollbar>
    </el-dialog>
  </span>
</template>

<style scoped lang="scss">
.menu-search {
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
