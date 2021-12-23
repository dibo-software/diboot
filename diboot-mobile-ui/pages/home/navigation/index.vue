<template>
	<view class="navigation">
		<view class="navigation-header u-border-bottom">
			<text class="navigation-header-title">标题</text>
			<view class="u-tips-color" @click.native="toListPage">
				<text>更多</text>
				<u-icon name="arrow-right"></u-icon>
			</view>
		</view>
		<view>
			<di-card
				v-for="(item,index) in list" 
				:key="item.id"
				:class="{'u-border-bottom':index !== list.length - 1}"
				:index="item.id"
				:title="item.title" 
				:image-list="item.imageList" 
				:mode="item.mode"
				@click="toDetail"
				>
				<view style="" slot="footer">
					{{item.date}}
				</view>
			</di-card>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [{
						id: 1,
						title: '内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1内容1',
						imageList: [require('@/static/logo.png')],
						date: '2021-11-22 10:27',
						mode: 'pictureCard'
					},
					{
						id: 2,
						title: '内容2',
						imageList: [require('@/static/logo.png'), require('@/static/logo.png'), require('@/static/logo.png')],
						date: '2021-11-22 10:27',
						mode: 'multiple'
					},
					{
						id: 3,
						title: '内容3',
						imageList: [require('@/static/logo.png')],
						date: '2021-11-22 10:27',
						mode: 'card'
					},
					{
						id: 4,
						title: '内容4',
						imageList: [require('@/static/logo.png')],
						date: '2021-11-22 10:27',
						mode: 'multiple'
					},
					{
						id: 5,
						title: '内容5',
						imageList: [require('@/static/logo.png')],
						date: '2021-11-22 10:27'
					}
				]
			}
		},
		methods:{
			/* 
		       跳转列表页面	 
			 */
			toListPage() {
				uni.navigateTo({
					url:'/pages/demo/list'
				})
			},
			/**
			 * 跳转详细页
			 */
			toDetail() {
				uni.navigateTo({
					url:'/pages/demo/detail'
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.navigation {
		background-color: #fff;
		height: 100%;
		&-header {
			padding: 24rpx;
			display: flex;
			justify-content: space-between;

			&-title {
				font-size: 32rpx;
				font-weight: bold;
			}
		}
	}
</style>
