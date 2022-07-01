<template>
	<view class="home page-bg-color u-rela">
		<view class="banner">
			<banner/>
		</view>
		<u-card margin="30rpx 0" padding="10" :border-radius="0" :border="false" :show-foot="false">
			<view class="" slot="head">
				<u-icon name="list-dot" class="u-margin-right-10 text"></u-icon>
				<text class="text">业务服务</text>
			</view>
			<u-grid slot="body" :col="4" :border="false">
				<u-grid-item :key="index" v-for="(menu, index) in menuList">
					<u-image width="64rpx" height="64rpx" :src="menu.icon"></u-image>
					<view class="menu-text text">{{menu.title}}</view>
				</u-grid-item>
			</u-grid>
		</u-card>
		<u-card margin="30rpx 0" padding="10" :border-radius="0" :border="false" :show-foot="false" title-size="28">
			<view class="" slot="head">
				<u-icon name="calendar-fill" class="u-margin-right-10 text"></u-icon>
				<text class="text">日程安排</text>
			</view>
			<calendar slot="body" mode="date"></calendar>
		</u-card>
		
	</view>
</template>

<script>
	import banner from './banner/index.vue'
	import calendar from './calendar/calendar.vue'
	export default {
		data() {
			return {
				show: true,
				menuList: [
					{
						title: '在线签到',
						icon: require('@/static/images/menu/signIn.png')
					},
					{
						title: '请假申请',
						icon: require('@/static/images/menu/leave.png')
					},
					{
						title: '出差申请',
						icon: require('@/static/images/menu/travel.png')
					},
					{
						title: '费用报销',
						icon: require('@/static/images/menu/expense.png')
					},
					{
						title: '企业内训',
						icon: require('@/static/images/menu/train.png')
					},
					{
						title: '采购申请',
						icon: require('@/static/images/menu/buy.png')
					},
					{
						title: '邮件通知',
						icon: require('@/static/images/menu/email.png')
					},
					{
						title: '流程审批',
						icon: require('@/static/images/menu/todo.png')
					}
				]
			};
		},
		components: {
			banner,
			calendar
		}
	}
</script>

<style lang="scss">
	page {
		height: unset;
	}
	.home {
		&-bg-color {
			top: 0;
			left: 0;
			width: 100%;
			height: 560rpx;
		}
		.text {
			font-weight: 400;
			font-size: 24rpx;
		}
		.menu-text {
			margin-top: 5rpx;
		}
	}
</style>
