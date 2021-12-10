<template>
	<view class="home page-bg-color u-rela u-p-24">
		<view class="u-abso home-bg-color"
			:style="{'background-image': `linear-gradient(${swiperBgColor}, rgba(255,255,255,0))`}"></view>
		<view class="u-m-b-40">
			<banner @setSwiperBgColor="setSwiperBgColor"></banner>
		</view>
		<view class="u-m-b-40">
			<di-scroll-menu-list :menu-list="menuList"></di-scroll-menu-list>
		</view>
		<view class="u-m-b-40 page-card" >
			<u-notice-bar padding="24rpx" type="success" mode="vertical" :list="noticeList"></u-notice-bar>
		</view>
		<view class="u-m-b-80 page-card">
			<navigation></navigation>
		</view>
	</view>
</template>

<script>
	import banner from './banner/index.vue'
	import navigation from './navigation/index.vue'
	export default {
		components: {
			banner,
			navigation
		},
		data() {
			return {
				swiperBgColor: '',
				noticeList: [
					'diboot-mobile-ui新版上线！',
					'diboot-devtools支持移动端生成！',
					'diboot-devtools支持统计图表生成！'
				],
				menuList: [
					{
						icon: require('@/static/logo.png'),
						title: 'list示例',
						path: '/pages/demo/list'
					},
					{
						icon: require('@/static/logo.png'),
						title: '表单示例',
						path:'/pages/demo/form'
					},
					{
						icon: require('@/static/logo.png'),
						title: '详细示例',
						path: '/pages/demo/detail'
					},
					{
						icon: require('@/static/logo.png'),
						title: '标题1'
					},
					{
						icon: require('@/static/logo.png'),
						title: '标题2'
					},
				]
			}
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
		&-bg-color {
			top: 0;
			left: 0;
			width: 100%;
			height: 560rpx;
		}
	}
</style>
