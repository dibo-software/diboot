<template>
	<view class="di-card" @click.native="handleClick">
		<!-- 多图卡片 -->
		<view v-if="mode === 'multiple'" class="card mode-multiple">
			<view class="card-content">
				<view class="card-content__title">
					<slot name="title">
						<text>{{title}}</text>
					</slot>
				</view>
				<view class="card-image">
					<view class="card-image__item" v-for="(item, index) in imageList" :key="index">
						<image class="el-image" :src="item" mode="aspectFill"></image>
					</view>
				</view>
				<view class="card-content__footer">
					<slot name="footer"></slot>
				</view>
			</view>
		</view>
		<!-- 大图模式/左图右文字模式 -->
		<view v-else class="card" :class="{'mode-picture-card': mode === 'pictureCard'}">
			<view class="card-image">
				<image class="el-image" :src="imageList[0]" mode="aspectFill"></image>
			</view>
			<view class="card-content">
				<view class="card-content__title">
					<slot name="title">
						<text>{{title}}</text>
					</slot>
				</view>
				<view class="card-content__footer">
					<slot name="footer"></slot>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	/**
	 * 图片卡片组件
	 * @description 适合文章列表的图片卡片组件
	 * @property {String Number} index 卡片唯一值
	 * @property {String} title 卡片标题
	 * @property {Array} image-list 卡片图片
	 * @property {String} mode = [default|pictureCard|multiple] 模式选择，"default"- 左图右文字（默认），"pictureCard"-大图模式，"multiple"-多图模式
	 * @event {Function} click
	 */
	export default {
		methods: {
			handleClick() {
				this.$emit('click', this.index)
			}
		},
		props: {
			index: {
				type: [String, Number],
				require: true
			},
			// 模式
			mode: {
				type: String,
				default: 'default'
			},
			title: {
				type: String,
				require: true
			},
			imageList: {
				type: Array,
				require: true
			}
		}
	}
</script>

<style lang="scss">
	.di-card {
		.card {
			display: flex;
			margin: 5px;
			padding: 10px;
			box-sizing: border-box;
			.card-image {
				width: 64px;
				height: 64px;
				border-radius: 5px;
				overflow: hidden;
				//避免挤压
				flex-shrink: 0;
				image {
					height: 100%;
					width: 100%;
				}
			}
			.card-content {
				display: flex;
				flex-direction: column;
				padding-left: 10px;
				width: 100%;
				justify-content: space-between;
				.card-content__title {
					position: relative;
					padding-right: 40px;
					font-size: 14px;
					font-weight: 400;
					color: #333;
					line-height: 1.2;
					//超过两行溢出隐藏
					text {
						overflow: hidden;
						text-overflow: ellipsis;
						display: -webkit-box;
						-webkit-line-clamp: 2;
						-webkit-box-orient: vertical;
					}
				}
				.card-content__footer {
					display: flex;
					justify-content: flex-end;
					align-items: center;
					font-size: 12px;
				}
			}
			&.mode-multiple {
				.card-content {
					padding-left: 0;
					width: 100%;
				}
				.card-image {
					display: flex;
					width: 100%;
					height: 70px;
					margin: 10px 0px;
					
					.card-image__item {
						display: flex;
						width: 100%;
						box-sizing: border-box;
						border-radius: 5px;
						margin-left: 10px;
						&:first-child {
							margin-left: 0;
						}
						.el-image {
							width: 100%;
							height: 100%;
						}
					}
				}
			}
			// 大图模式
			&.mode-picture-card {
				flex-direction: column;
				.card-image {
					width: 100% ;
					height: 100px;
				}
				.card-content {
					margin-top: 10px;
					padding-left: 0;
					.card-content__footer {
						margin-top: 10px;
					}
				}
			}
		}
	}
</style>
