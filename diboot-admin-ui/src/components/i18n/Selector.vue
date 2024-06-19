<script setup lang="ts">
import I18nList from '@/views/system/i18n-config/List.vue'
import type { I18nConfig } from '@/views/system/i18n-config/type'
import type { Ref } from 'vue'

const props = defineProps<{ modelValue?: string; showContent?: boolean }>()
const emits = defineEmits<{
  (e: 'update:modelValue', code?: string): void
}>()
const value = ref(props.modelValue)

watch(
  () => props.modelValue,
  code => (value.value = code)
)

const visible = ref(false)

const dataList: Ref<I18nConfig[] | undefined> = ref([])
const loading = ref(false)

watch(
  value,
  code => {
    if (code) {
      if (dataList.value?.length === 0) {
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
        <span style="cursor: pointer" @click="visible = true">
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
    <el-dialog v-model="visible" top="10vh" width="1000px" append-to-body>
      <template #header>
        <div style="display: flex; justify-content: space-between; margin-right: 30px">
          <span class="el-dialog__title">{{ $t('components.i18n.config') }}</span>
          <el-button v-show="modelValue" text type="danger" size="small" @click="value = undefined"
            >{{ $t('button.cancel') }}{{ $t('button.select') }}</el-button
          >
        </div>
      </template>
      <i18n-list v-model="value" table-height="500" select class="i18n-list" @change="list => (dataList = list)" />
    </el-dialog>
  </span>
</template>

<style scoped lang="scss">
.i18n-list {
  :deep(.el-row) {
    margin: 0 !important;

    .el-row {
      margin-top: -9px !important;

      .el-col {
        margin-top: 9px;
      }
    }
  }
}
</style>
