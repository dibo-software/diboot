<template>
	<view class="personal page-bg-color">
		<view class="personal-header">
			<u-avatar :src="require('@/static/logo.png')" size="large"></u-avatar>
			<view class="personal-header-info">
				<text class="personal-header-info-name">{{userInfo.displayName || '-'}}</text>
				<text class="u-tips-color">{{userInfo.displayName || '-'}}</text>
			</view>
			<u-icon size="40" name="edit-pen"></u-icon>
		</view>
		<view class="u-m-t-20">
			<u-cell-group>
				<u-cell-item icon="weixin-fill" title="绑定微信" @click="weiBind"></u-cell-item>
			</u-cell-group>
		</view>
		<view class="u-m-t-80">
			<u-button type="error" @click="logout">退出登录</u-button>
		</view>
		<u-top-tips ref="uTips"></u-top-tips>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				userInfo: JSON.parse(uni.getStorageSync('userInfo') || '{}')
			}
		},
		methods: {
			logout() {
				const that = this
				that.$dibootApi.post('/h5/logout').then(() => {
					that.$tip(that.$refs.uTips, '退出成功!').then(() => {
						uni.clearStorageSync()
						let timer = setTimeout(() => {
							clearTimeout(timer)
						      uni.reLaunch({
						      	url: '/pages/login/index'
						      });
						  }, 0)
						
					})
				})
			},
			//
			/**
			 * 微信绑定
			 */
			weiBind() {
				// 小程序登陆
				// #ifdef MP-WEIXIN
				this.$miniLogin.setBindWx(true).setTip(this.$refs.uToast).setUrlPath('/pages/personal/personal').go()
				//#endif
				// 微信公众号登陆
				// #ifdef H5
				this.$mpLogin.redirect(true)
				//#endif
			}
		},
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
	}
</style>
