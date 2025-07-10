<script setup lang="ts">
import type { Client } from './type'

const baseApi = '/iam/client'

const { loadData, loading, model } = useDetail<Client>(baseApi)

defineExpose({ init: loadData })
</script>

<template>
  <el-empty v-if="!Object.keys(model).length" v-loading="loading" />
  <el-descriptions v-else v-loading="loading" :column="2" border>
    <el-descriptions-item :label="$t('client.name')" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.name }}
      </span>
    </el-descriptions-item>
    <el-descriptions-item :label="$t('client.status')" label-class-name="item-label" label-align="right">
      <el-tag v-if="model.statusLabel" :color="model.statusLabel.ext?.color" effect="dark" type="info">
        {{ model.statusLabel?.label }}
      </el-tag>
    </el-descriptions-item>
    <el-descriptions-item label="App Key" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.appKey }}
      </span>
    </el-descriptions-item>
    <el-descriptions-item label="App Secret" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.appSecret }}
      </span>
    </el-descriptions-item>
    <el-descriptions-item :label="$t('client.permissions')" :span="2" label-class-name="item-label" label-align="right">
      <div style="word-wrap: break-word; width: calc(60vw - 130px)">
        {{ model.permissions?.join(',') }}
      </div>
    </el-descriptions-item>
    <el-descriptions-item :label="$t('baseField.createTime')" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.createTime }}
      </span>
    </el-descriptions-item>
    <el-descriptions-item :label="$t('baseField.updateTime')" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.updateTime }}
      </span>
    </el-descriptions-item>
  </el-descriptions>
</template>

<style scoped lang="scss">
.el-descriptions {
  :deep(.el-descriptions__header) {
    margin: 0 10px 12px;
  }

  :deep(.item-label) {
    width: 120px;
  }
}
</style>
