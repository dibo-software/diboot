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
						title: 'diboot 是一套全新的基于"坚固地基(基础框架)+高效脚手架(devtools代码生成工具)"的低代码开发平台，致力于解决开发的质量效率和可维护难题。',
						imageList: [require('@/static/images/diboot/diboot-lowcode.jpeg')],
						date: '2022-03-22 10:27',
						mode: 'pictureCard'
					},
					{
						id: 2,
						title: 'diboot-cloud，基于diboot spring boot版本打造，将diboot的优势延伸到微服务开发场景。',
						imageList: [require('@/static/images/diboot/diboot-cloud.jpeg')],
						date: '2022-03-22 10:27',
						mode: 'card'
					},
					{
						id: 3,
						title: '结合diboot平台的架构设计经验优势，匠心打磨出了 diboot-workflow 项目，助力企业客户基于我们的源码，跳过沼泽地，高效无风险落地流程审批业务。',
						imageList: [require('@/static/images/diboot/diboot-workflow.png'), require('@/static/images/diboot/diboot-workflow2.png')],
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
