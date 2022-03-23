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
						title: '卡片组件-大图',
						imageList: [require('@/static/images/3.png')],
						date: '2022-03-22 10:27',
						mode: 'pictureCard'
					},
					{
						id: 2,
						title: '卡片组件-小图',
						imageList: [require('@/static/images/2.png')],
						date: '2022-03-22 10:27',
						mode: 'card'
					},
					{
						id: 3,
						title: '卡片组件-多图',
						imageList: [require('@/static/images/1.png'), require('@/static/images/2.png')],
						date: '2022-03-22 10:27',
						mode: 'multiple'
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
				font-size: 28rpx;
				font-weight: bold;
			}
		}
	}
</style>
