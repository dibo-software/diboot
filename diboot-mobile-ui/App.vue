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
	.di-scroll {
		width: 100%;
		height: 100%;
		&-list {
			background-color: #fff;
			border-radius: 20rpx;
			overflow: hidden;
			margin: 20rpx;
		}
		&_create {
			width: 100%;
			position: fixed;
			left: 0;
			bottom: 100rpx;
			padding: 0 24rpx;
		}
	}
</style>
