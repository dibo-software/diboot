<template>
	<view class="home page-bg-color u-rela u-p-24">
		<view class="u-m-b-30 menu-color">
			<di-scroll-menu-list :menu-list="menuList"></di-scroll-menu-list>
		</view>
		<view class="u-m-b-40 page-card">
			<navigation></navigation>
		</view>
	</view>
</template>

<script>
	import navigation from './navigation/index.vue'
	export default {
		components: {
			navigation
		},
		data() {
			return {
				swiperBgColor: '',
				menuList: [
					{
						icon: require('@/static/images/list.png'),
						title: 'list示例',
						path: '/pages/component-page/crud/list'
					},
					{
						icon: require('@/static/images/form.png'),
						title: '表单示例',
						path:'/pages/component-page/crud/form'
					},
					{
						icon: require('@/static/images/detail.png'),
						title: '详细示例',
						path: '/pages/component-page/crud/detail'
					},
					{
						icon: require('@/static/logo.png'),
						title: '测试'
					},
					{
						icon: require('@/static/logo.png'),
						title: '测试'
					}
				]
			}
		},
		onLoad() {
			this.$member.getMemberInfo()
		},
		methods: {
			/* 
			 设置轮播图的背景颜色
			 */
			setSwiperBgColor(color) {
				this.swiperBgColor = color
			}
		}
	}
</script>

<style scoped lang="scss">
	page {
		height: 100%;
	}
	.home {
		height: 100%;
		&-bg-color {
			top: 0;
			left: 0;
			width: 100%;
			height: 560rpx;
		}
		.menu-color {
			background-color: #fff;
			border-radius: 10rpx;
			padding: 30rpx 0;
		}
	}
</style>
