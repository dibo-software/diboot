<script setup lang="ts">
import lowCode from '@/assets/image/low-code.png'
</script>

<template>
  <van-space direction="vertical" size="16px" fill>
    <van-image width="calc(100% - 40px)" class="cover" :src="lowCode" height="172" />

    <van-notice-bar
      color="#558cda"
      background="#e2ecf9"
      style="height: 35px; border-radius: 10px; margin: 0 20px; padding: 0 10px; letter-spacing: 1px; font-size: 13px"
    >
      <template #left-icon>
        <Icon name="Notice" size="28" style="margin-right: 6px" />
      </template>
      先进的工作流体系、延申至微服务场景
    </van-notice-bar>

    <div>
      <div class="title" style="background-color: var(--van-cell-background)">业务服务</div>

      <van-grid style="padding: 0 15px; background-color: var(--van-background-2)">
        <van-grid-item icon="photo-o" text="发起流程">
          <template #icon>
            <Icon name="ProcessInitiate" size="var(--van-grid-item-icon-size)" />
          </template>
        </van-grid-item>
        <van-grid-item icon="photo-o" text="我发起的">
          <template #icon>
            <Icon name="ProcessInitiated" size="var(--van-grid-item-icon-size)" />
          </template>
        </van-grid-item>
        <van-grid-item
          icon="records"
          icon-color="#436ef6"
          text="我的待办"
          :badge="10"
          :badge-props="{ showZero: false }"
        >
          <template #icon>
            <Icon name="ProcessTodo" size="var(--van-grid-item-icon-size)" />
          </template>
        </van-grid-item>
        <van-grid-item icon="photo-o" text="我的已办">
          <template #icon>
            <Icon name="ProcessDone" size="var(--van-grid-item-icon-size)" />
          </template>
        </van-grid-item>
      </van-grid>
    </div>

    <div>
      <div class="title" style="padding: 10px 0; border-bottom: 0.3px solid #cccccc50">常用流程</div>

      <van-list style="letter-spacing: 1px">
        <van-cell
          v-for="(item, index) in [{ name: '请假流程' }, { name: '报销流程' }, { name: '采购流程' }]"
          :key="index"
          :title="item.name"
          style="padding: var(--van-cell-vertical-padding) 24px"
        >
          <template #icon>
            <Icon name="ProcessStart" size="24px" style="margin-right: 5px" />
          </template>
          <template #title>
            <van-text-ellipsis :content="item.name" />
          </template>
        </van-cell>
      </van-list>
    </div>
  </van-space>
</template>

<style scoped lang="scss">
.van-space {
  .cover {
    display: block;
    margin: 15px 20px 0 20px;
    box-shadow: 1px 1px 5px 1px #ccc;

    &,
    :deep(img) {
      border-radius: 10px;
    }
  }

  .van-hairline--top,
  .van-grid-item {
    &,
    :deep(.van-hairline) {
      position: static;
    }
  }

  :deep(.van-space-item:nth-child(3)) {
    padding-bottom: 12px;
    margin-bottom: 0 !important;
    background-color: var(--van-background);
  }

  .title {
    height: 20px;
    display: flex;
    align-items: center;
    font-weight: bold;

    &:before {
      width: 3px;
      height: 100%;
      margin: 0 10px;
      border-radius: 2px;
      background-color: #3868ad;
      content: ' ';
    }
  }
}
</style>
