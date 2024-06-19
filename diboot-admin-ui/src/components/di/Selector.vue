<script setup lang="ts" name="DiListSelector">
import type { Input, ListConfig, ListSelector, TreeConfig } from '@/components/di/type'
import type { RelatedData } from '@/hooks/use-option'

type ModelValue = string | string[]

interface ListSelectorProps extends /* @vue-ignore */ Omit<ListSelector, keyof Omit<Input, 'placeholder'> | 'type'> {
  modelValue?: ModelValue
  placeholder?: string
  disabled?: boolean

  // vue语法限制导致只能在当前文件中再次定义
  // https://cn.vuejs.org/guide/typescript/composition-api.html#typing-component-props
  tree?: Omit<TreeConfig, 'sortApi'>
  list?: Omit<ListConfig, 'operation'>
  dataType: string
  dataLabel?: string
  multiple?: boolean
  rootId?: string
}

const props = defineProps<ListSelectorProps>()

const config = { tree: props.tree, list: props.list }

const specifyRootNode = () => {
  if (!config.tree || !props.rootId) return
  const tree = (config.tree = _.cloneDeep(config.tree))
  ;(tree.conditions ?? (tree.conditions = [])).push({ field: tree.parent, value: props.rootId })
}

// if (!config.list) {
//
// }
specifyRootNode()

provide('multiple', !!props.multiple)

const selectedKeys = ref<string | string[] | undefined>(props.modelValue)
const selectedList = ref<LabelValue[]>([])

const selectedRows = ref<LabelValue[]>([])
provide('selected-rows', selectedRows)
const dataLabel = props.dataLabel ?? (config.list?.columns[0] ?? { prop: 'label' })?.prop
provide('data-label', dataLabel)

const selected: RelatedData = reactive({ type: props.dataType, label: dataLabel })

const { loadRelatedData } = useOption({})

// 选择器显隐
const visible = ref(false)
const open = (val = true) => {
  if (val) visible.value = val
}

const confirm = () => {
  selectedList.value = _.clone(selectedRows.value)
  if (!selectedList.value?.length) {
    selectedKeys.value = props.multiple ? [] : undefined
  } else if (props.multiple) {
    selectedKeys.value = selectedList.value.map(e => `${e.value}`)
  } else {
    selectedKeys.value = selectedList.value[0].value
  }
  visible.value = false
}

const cancel = () => {
  visible.value = false
  selectedRows.value = _.clone(selectedList.value)
  emit('close')
}

const removeTag = (val: string) => (selectedRows.value = selectedRows.value.filter(e => e.value !== val))

const remove = (val: string) => {
  const keys = selectedKeys.value as string[]
  const index = keys.indexOf(val)
  keys.splice(index, 1)
  selectedRows.value.splice(index, 1)
  selectedList.value.splice(index, 1)
}

const clear = () => {
  selectedKeys.value = props.multiple ? [] : undefined
  selectedRows.value.length = 0
  selectedList.value.length = 0
}

watch(
  () => props.modelValue,
  (newValue, oldValue) => {
    if (`${newValue}` === `${oldValue}`) return
    else if (newValue && newValue.length) {
      nextTick(() => {
        const values = Array.isArray(newValue) ? newValue : [newValue]

        const buildList = () => {
          const list = [] as LabelValue[]
          for (const id of values) {
            const item = selectedList.value.find(e => e.value === id)
            if (item) list.push(item)
            else return
          }
          return list
        }
        // 现有数据构建回显，无法构建时异步获取
        const list = buildList()
        if (list) {
          selectedRows.value = list
          confirm()
        } else {
          selected.conditions = [{ field: config.list?.primaryKey || 'id', comparison: 'IN', value: values }]
          loadRelatedData(selected).then(list => {
            selectedRows.value = list.sort((e1, e2) => values.indexOf(e1.value) - values.indexOf(e2.value))
            confirm()
          })
        }
      })
    } else {
      selectedRows.value.length = 0
      confirm()
    }
  },
  { immediate: true }
)

const emit = defineEmits<{
  (e: 'update:modelValue', modelValue?: ModelValue): void
  (e: 'change', modelValue?: ModelValue): void
  (e: 'close'): void
}>()

watch(
  selectedKeys,
  value => {
    emit('update:modelValue', value)
    emit('change', value)
  },
  { deep: true }
)

defineExpose({ open, clear })

const parent = ref<string | undefined>(props.rootId)
const clickNode = (id?: string) => (parent.value = id ?? props.rootId)
</script>

<template>
  <slot v-bind="{ open, clear, list: selectedList }">
    <el-select
      :model-value="selectedKeys"
      clearable
      :multiple="multiple"
      popper-class="hide"
      :placeholder="placeholder"
      :disabled="disabled"
      @remove-tag="remove"
      @clear="clear"
      @visible-change="open"
    >
      <el-option v-for="item in selectedList" :key="item.value" :value="item.value" :label="item.label" />
    </el-select>
  </slot>

  <el-dialog v-model="visible" top="3vh" :width="config.tree ? '75%' : ''" append-to-body @close="cancel">
    <template #header>
      <div style="display: flex">
        <div style="zoom: 1.1; margin-right: 10px">{{ $t('components.di.selector.label') }}</div>
        <el-space
          wrap
          :style="config.tree ? { width: 'calc(100% - 260px)', marginLeft: '200px' } : { width: 'calc(100% - 60px)' }"
        >
          <el-tag v-for="(item, index) in selectedRows" :key="index" closable @close="removeTag(item.value)">
            {{ item.label }}
          </el-tag>
        </el-space>
      </div>
    </template>
    <div class="body-container">
      <di-tree v-if="config.tree" v-bind="config.tree" :sort-api="undefined" @click-node="clickNode" />

      <di-list
        v-if="config.list"
        hidden-action-column
        :base-api="config.list.baseApi as string"
        :primary-key="config.list.primaryKey"
        :related-key="config.list.relatedKey"
        :search-area="config.list.searchArea"
        :columns="config.list.columns"
        :model="dataType"
        :operations="[]"
        :parent="parent"
        style="width: 0"
      />
    </div>
    <template #footer>
      <el-button size="default" @click="cancel">{{ $t('button.cancel') }}</el-button>
      <el-button size="default" type="primary" @click="confirm">{{ $t('button.confirm') }}</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.body-container {
  height: 80vh;
  display: flex;
  position: relative;
  margin-top: -10px;
  margin-bottom: -10px;
}
</style>
