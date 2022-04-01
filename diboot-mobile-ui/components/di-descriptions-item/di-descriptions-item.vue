<template>
	<u-row class="di-descriptions-item" align="top">
		<u-col :span="labelCol || parentCol.labelCol || 3" class="di-descriptions-item__label">
			<slot name="label">
				{{label ? label + ':' : ''}}
			</slot>
		</u-col>
		<u-col :span="valueCol || parentCol.valueCol || 9" class="di-descriptions-item__value">
			<slot name="value">
				<view :class="{'di-descriptions-item__ellipsis': ellipsis}">
				{{value || ''}}
				</view>
			</slot>
		</u-col>
	</u-row>
</template>

<script>
	/**
	* di-descriptions-item 描述列表项
	* @description 描述列表项，用于展示label value的值，比如对象详细页面等。 搭配父组件di-descriptions
	* @property {String slot} label label
	* @property {String Number slot} value value
	* @property {Boolean} ellipsis value过长是否显示省略（默认false）
	* @property {Number String} label-col label宽度，等分12份，可以继承di-descriptions#label-col，默认值3
	* @property {Number String} value-col value宽度，等分12份，可以继承di-descriptions#value-col，默认值9
	*/
	export default {
		computed: {
			parentCol() {
				if(this.descriptions && this.descriptions.$options && this.descriptions.$options.propsData) {
					let {labelCol, valueCol} = this.descriptions.$options.propsData
					return {labelCol, valueCol}
				}
				return {}
			},
			descriptions() {
			  let parent = this.$parent;
			  let parentName = parent.$options._componentTag;
			  while (parentName !== 'di-descriptions') {
				if(!(parent = parent.$parent)) {
					break
				}
			    parentName = parent.$options._componentTag;
			  }
			  return parent;
			}
		},
		props: {
			label: {
				type: String,
				require: true
			},
			value: {
				type: [String, Number],
				require: true
			},
			ellipsis: {
				type: Boolean,
				default: false
			},
			labelCol: {
				type: [String, Number]
			},
			valueCol: {
				type: [String, Number]
			}
		}
	}
</script>

<style lang="scss">
.di-descriptions-item {
	color: $u-content-color;
	margin-bottom: 10rpx;
	&__label {
		color: #a3a3a3;;
	}
	&__ellipsis {
		overflow: hidden;
		text-overflow: ellipsis;
		white-space: nowrap;
	}
}
</style>
