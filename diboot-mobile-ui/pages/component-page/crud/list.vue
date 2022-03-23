<template>
	<view class="h100 page-bg-color" :style="{paddingTop: diStatusBarHeight + 44 + 'px'}">
		<u-navbar title="列表" height="44" :immersive="true" :background="{background: '#f8f8f8'}" :border-bottom="false">
			<u-icon @click="handleCreate" style="margin-right: 40rpx;" slot="right" name="plus" size="28" label="新建" color="#19be6b" label-color="#19be6b"/>
		</u-navbar>
		<scroll-view class="di-scroll" scroll-y  @scrolltolower="handleOnreachBottom" :refresher-triggered="triggered"
			refresher-enabled @refresherrefresh="handlePullDownRefresh">
			<view class="di-scroll-list">
				<!-- 右滑 -->
				<u-swipe-action
					v-for="(item, index) in list"
					:show="activeIndex === item[primaryKey]"
					:key="index" 
					:index='item.id'
					:options="actionOptions"
					@content-click="handleDetail"
					@click="handleActionClick"
					@open="handleActiveSwipeAction">
					<di-descriptions :title="item.title" label-col="4" value-col="8" :border-bottom="true" >
						<di-descriptions-item label="文字省略" value="你好你好你好你好你好你好你好你好你好你好你好你好你好你好" :ellipsis="true"/>
						<di-descriptions-item label="createTime" :value="item.createTime"/>
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
		 @cancel="handleCancelDel">
		</u-modal>
	</view>
</template>

<script>
	import list from '@/mixins/list'
	export default {
		mixins: [list],
		methods: {
			/**
			 * 获取数据列表 (重写函数)
			 */
			async getList(replace = false) {
				let count = 10000
				setTimeout(() => {
					let list = []
					for (let i = 1; i <= this.page.pageSize; i++) {
						list.push({
							id: ++count,
							title: '列表展示 ' + count,
							createTime: '2021-11-22 10:27'
						})
					}
					this.list = replace ? list : this.list.concat(list)
					this.page.pageIndex++
					this.triggered = false
				}, 2000)
			}
		}
	}
</script>
<style scoped lang="scss">
</style>
