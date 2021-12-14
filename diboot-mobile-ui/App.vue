<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
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
			background-color: #fff;
			border-radius: 20rpx;
			overflow: hidden;
			margin: 20rpx;
			margin-top: 100rpx;
		}
	}
</style>
