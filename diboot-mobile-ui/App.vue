<script>
	export default {
		onShow: function() {
			if(uni.getStorageSync("authtoken")) {
				let bindWpTag = uni.getStorageSync("bindWpTag")				
				// 如果是发起绑定
				if(bindWpTag) {
					this.$mpLogin
					.bindWxMp()
					.then(() => {
						// 刷新个人页面
						uni.reLaunch({
							url: '/pages/personal/personal'
						})
					})
				}
			} else {
				let redirect = uni.getStorageSync("redirect")
				if(redirect) {
					this.$mpLogin
					.setTip(this.$refs.uTips)
					.go()
					.then(() => {
						// 跳转到首页
						uni.switchTab({
							url: '/pages/home/home'
						})
					})
				} else {
					// 直接redirectTo/reLaunch会导致小程序点击事件无法使用，需要增加延迟
					// reLaunch H5中会导致表单校验失效
					let timer = setTimeout(() => {
						clearTimeout(timer)
					      uni.redirectTo({
					          url: 'pages/login/index'
					      })
					  }, 0)
				}

			}
		}
	}
</script>

<style lang="scss">
	@import "uview-ui/index.scss";

	/*每个页面公共css */
	page, .h100  {
		height: 100%;
	}
	.page-bg-color {
		background-color: $u-bg-color;
	}
	.page-card {
		border-radius: 20rpx;
		background-color: #fff;
		overflow: hidden;
	}
	.di-scroll {
		width: 100%;
		height: 100%;
		&-list {
			box-sizing: border-box;
			background-color: #fff;
			border-radius: 20rpx;
			overflow: hidden;
			margin: 20rpx;
		}
	}
</style>
