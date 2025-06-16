<script setup lang="ts">
import type { Group } from './type'

const baseApi = '/iam/group'

const { loadData, loading, model } = useDetail<Group>(baseApi)

defineExpose({ init: loadData })
</script>

<template>
  <el-empty v-if="!Object.keys(model).length" v-loading="loading" />
  <el-descriptions v-else v-loading="loading" :column="1" border>
    <el-descriptions-item :label="$t('group.name')" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.name }}
      </span>
    </el-descriptions-item>
    <el-descriptions-item :label="$t('group.orgId')" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.orgLabel }}
      </span>
    </el-descriptions-item>
    <el-descriptions-item :label="$t('group.members')" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.membersLabel?.join('、') }}
      </span>
    </el-descriptions-item>
    <el-descriptions-item :label="$t('group.description')" label-class-name="item-label" label-align="right">
      <span class="txt-short">
        {{ model.description }}
      </span>
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
