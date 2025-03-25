<script setup lang="ts">
import I18nList from '@/views/system/i18n-config/List.vue'
import type { I18nConfig } from '@/views/system/i18n-config/type'
import type { Ref } from 'vue'

const props = defineProps<{
  modelValue?: string
  showContent?: boolean
  group?: Ref<string> | string
  suffix?: Ref<string> | string
}>()

provide('i18n-suffix', () => unref(props.suffix))

const emits = defineEmits<{
  (e: 'update:modelValue', code?: string): void
}>()
const value = ref(props.modelValue)

watch(
  () => props.modelValue,
  code => (value.value = code)
)

const visible = ref(false)

const codes = inject<Array<string> | undefined>('i18n-codes', void 0)
const dataList: Ref<I18nConfig[] | undefined> = ref([])
const loading = ref(false)

watch(
  value,
  (code, old) => {
    if (codes) {
      const oldIndex = codes.findIndex(e => e === old)
      if (oldIndex >= 0) codes.splice(oldIndex, 1)
      if (code) codes.push(code)
    }
    if (code) {
      if (!dataList.value?.length || dataList.value[0].code != code) {
        loading.value = true
        dataList.value = []
        api
          .get<I18nConfig[]>(`/i18n-config/${code}`)
          .then(res => {
            dataList.value = res.data ?? []
          })
          .finally(() => (loading.value = false))
      }
    } else {
      dataList.value = []
    }
    emits('update:modelValue', code)
    visible.value = false
  },
  { immediate: true }
)

const i18nGroup = inject<Ref<string> | undefined>('i18n-group', void 0)

const listRef = ref()

watch(visible, boo => {
  if (boo) {
    let queryCode = unref(props.group ?? i18nGroup)
    if (!queryCode) {
      ;(value.value ? [value.value] : (codes ?? []))
        .map(val => val?.slice(0, val.lastIndexOf('.') + 1))
        .filter(e => !!e)
        .reduce((map: Record<string, any>, code) => {
          map[code] ? (map[code] += 1) : (map[code] = 1)
          if (map[code] > (map._max ?? 0) && (map._max = map[code])) queryCode = code
          return map
        }, {})
    }
    nextTick(() => listRef.value?.refresh(queryCode))
  }
})
</script>

<template>
  <span>
    <el-popover
      :title="`${$t('components.i18n.language')}：${value}`"
      width="auto"
      trigger="hover"
      :disabled="!value"
      :show-after="150"
    >
      <template #reference>
        <span style="cursor: pointer" @click.stop="visible = true">
          <el-icon
            :size="18"
            style="vertical-align: middle; margin-right: 10px"
            :color="value ? 'var(--el-color-primary)' : ''"
          >
            <icon name="Local:Language" />
          </el-icon>
          <span v-if="showContent">
            {{ dataList?.find(e => e.language === $i18n.locale.replace(/-/g, '_'))?.content }}
          </span>
        </span>
      </template>
      <el-descriptions v-loading="loading" :column="1" size="small">
        <el-descriptions-item
          v-for="locale in $i18n.availableLocales"
          :key="locale"
          :label="$t('language', {}, { locale })"
        >
          {{ dataList?.find(e => e.language === locale.replace(/-/g, '_'))?.content }}
        </el-descriptions-item>
      </el-descriptions>
    </el-popover>
    <el-dialog v-model="visible" top="10vh" width="1000px" append-to-body draggable>
      <template #header>
        <div style="display: flex; justify-content: space-between; margin-right: 30px">
          <span class="el-dialog__title">{{ $t('components.i18n.config') }}</span>
          <el-button v-show="modelValue" plain type="danger" size="small" @click="value = undefined">
            {{ $t('button.cancel') }}{{ $t('button.select') }}
          </el-button>
        </div>
      </template>
      <i18n-list ref="listRef" v-model="value" table-height="500" select @change="list => (dataList = list)" />
    </el-dialog>
  </span>
</template>
