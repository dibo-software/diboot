<template>
	<u-cell-group class="di-descriptions" :border="border" :class="{'u-border-bottom': borderBottom}">
		<u-cell-item :arrow="false" :use-label-slot='true' :border-bottom='false' :border-top="false">
			<view slot="title" class="di-descriptions__title" :class="{'u-border-bottom': titleBottom}">
				<view class="di-descriptions__title-left">
					<slot name="title">
						{{title}}
					</slot>
				</view>
				<view class="di-descriptions__title-right">
					<slot name="right"></slot>
				</view>
			</view>
			<view slot="label" class="di-descriptions__body">
				<slot></slot>
			</view>
		</u-cell-item>
	</u-cell-group>
</template>

<script>
	/**
	* di-descriptions 描述列表
	* @description 描述列表，用于展示label value的值，比如对象详细页面等。 搭配 di-descriptions-item
	* @property {String slot} title 头部信息，也可以使用插槽
	* @property {String slot} right 插槽
	* @property {Boolean} border 是否显示边框（默认false）
	* @property {Boolean} border-bottom 是否显示底部边框，与border分开使用，单位rpx（默认false）
	* @property {Boolean} title-bottom 是否显示title底部边框，单位rpx（默认false）
	* @property {Number String} label-col label宽度，等分12份，设置后子元素di-descriptions-item可以继承
	* @property {Number String} value-col value宽度，等分12份，设置后子元素di-descriptions-item可以继承
	*/
	export default {
		name: 'DiDescriptions',
		props: {
			title: {
				type: String | Number
			},
			border: {
				type: Boolean,
				default: false
			},
			borderBottom: {
				type: Boolean,
				default: false
			},
			titleBottom: {
				type: Boolean,
				default: false
			},
			labelCol: {
				type: String | Number,
				default: 3
			},
			valueCol: {
				type: String | Number,
				default: 9
			}
		}
	}
</script>

<style lang="scss">
.di-descriptions {
	/deep/.u-cell_title {
		width: 100% !important;
	}
	&__title {
		font-weight: bold;
		display: flex;
		justify-content: space-between;
	}
	
}
</style>
