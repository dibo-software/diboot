<template>
	<u-cell-group class="di-descriptions" :border="border">
		<u-cell-item :arrow="false" :use-label-slot='true' :border-bottom='borderBottom' :border-top="false">
			<template #title>
			<view class="di-descriptions__title" :class="{'u-border-bottom': titleBottom}">
				<view class="di-descriptions__title-left">
					<slot name="title">
						<text v-if="title">{{title}}</text>
					</slot>
				</view>
				<view class="di-descriptions__title-right">
					<slot name="right"></slot>
				</view>
			</view>
			</template>
			<template slot="label">
				<view class="di-descriptions__body">
					<slot></slot>
				</view>
			</template>
		</u-cell-item>
	</u-cell-group>
</template>

<script>
	/**
	* di-descriptions 描述列表
	* @description 描述列表，用于展示label value的值，常见于详情页的信息展示。 搭配 di-descriptions-item
	* @property {String slot} title 头部信息，也可以使用插槽
	* @property {String slot} right 插槽
	* @property {Boolean} border 是否显示边框（默认false）
	* @property {Boolean} border-bottom 是否显示底部边框，与border分开使用，单位rpx（默认false）
	* @property {Boolean} title-bottom 是否显示title底部边框，单位rpx（默认false）
	* @property {Number String} label-col label宽度，等分12份，设置后子元素di-descriptions-item可以继承
	* @property {Number String} value-col value宽度，等分12份，设置后子元素di-descriptions-item可以继承
	*/
	export default {
		// #ifdef MP-WEIXIN
		// 解决小程序样式无法使用
		options: { styleIsolation: 'shared' },
		// #endif
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
	// width: 100%;
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
