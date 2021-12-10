<template>
	<view class="personal page-bg-color">
		<view class="personal-header">
			<u-avatar src="" size="large"></u-avatar>
			<view class="personal-header-info">
				<text class="personal-header-info-name">张三</text>
				<text class="u-tips-color">管理员</text>
			</view>
			<u-icon size="40" name="edit-pen"></u-icon>
		</view>
		<view class="personal-cell">
			<cell :list="cellList"></cell>
		</view>
		<view class="u-m-t-80">
			<u-button type="error" @click="logOut">退出登录</u-button>
		</view>
		<u-top-tips ref="uTips"></u-top-tips>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				cellList: [{
						title: '标题1',
					},
					{
						title: '标题2',
					},
					{
						title: '标题3',
					}
				]
			}
		},
		methods: {
			logOut() {
				this.$tip(this.$refs.uTips, '退出登陆成功').then(() => {
					uni.reLaunch({
						url: '/pages/login/index'
					});
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.personal {
		padding: 24rpx 24rpx 0;

		&-header {
			display: flex;
			background-color: #fff;
			padding: 40rpx;
			border-radius: 20rpx;
			margin-bottom: 40rpx;

			&-info {
				flex: 1;
				display: flex;
				flex-direction: column;
				justify-content: space-around;
				margin-left: 40rpx;

				&-name {
					font-weight: bold;
					font-size: 32rpx;
				}
			}
		}

		&-cell {
			border-radius: 20rpx;
			overflow: hidden;
		}
	}
</style>
