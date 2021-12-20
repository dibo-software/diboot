<template>
  	<view class="h100 page-bg-color">
	  <u-navbar title="列表" :immersive="true" :background="{background: '#f8f8f8'}" :border-bottom="false">
		  <u-icon @click="handleCreate" style="margin-right: 40rpx;" slot="right" name="plus" size="28" label="新建" :color="$color.success" :label-color="$color.success"/>
	  </u-navbar>
      <scroll-view class="di-scroll" scroll-y  @scrolltolower="handleOnreachBottom" :refresher-triggered="triggered"
        refresher-enabled @refresherrefresh="handlePullDownRefresh">
        <view class="di-scroll-list">
          <u-swipe-action
			v-for="(item, index) in list"
		    :show="activeIndex === item[primaryKey]"
			:key="index" 
			:index='item.id'
			:options="actionOptions"
			@content-click="handleDetail"
			@click="handleActionClick"
			@open="handleActiveSwipeAction">
			   <di-descriptions label-col="4" value-col="8" :border-bottom="true">
					<di-descriptions-item label="单行" :value="item.testInput" />
				  <di-descriptions-item label="多行" :value="item.testTextarea" :ellipsis="true"/>
				  <di-descriptions-item label="多选select" :value="item.testMultiSelect" />
				  <di-descriptions-item label="树结构" :value="item.testTreeSelect" />
				  <di-descriptions-item label="忽略不显示" :value="item.testIgnore" />
				  <di-descriptions-item label="创建时间" :value="item.createTime" />
				  <di-descriptions-item label="单选select" :value="item.testSingleSelectLabel" />
				  <di-descriptions-item label="checkbox" :value="item.testCheckboxLabel" />
				 </di-descriptions>
			</u-swipe-action>
        </view>
        <u-loadmore v-if="!triggered" :status="status" :loadText='loadText' margin-top="24" margin-bottom="24" />
      </scroll-view>
      <u-modal 
        v-model="deleteShow"
        title="删除"
        :show-cancel-button="true"
        :confirm-color="$color.error"
        confirm-content="确认要删除吗?"
        @confirm="handleConfirmDel"
        @cancel="handleCancelDel"/>
		</view>
</template>

<script>
import list from '@/mixins/list'
export default {
  mixins: [list],
  data () {
    return {
      baseApi: '/testAllField',
      getListFromMixin: true,
	  attachMoreList: [
	    {
	      type: 'D',
	      target: 'GENDER'
	    },
	    {
	      type: 'D',
	      target: 'ORDER_STATUS'
	    }
	  ]
    }
  }

}
</script>
<style scoped lang="scss">
</style>
