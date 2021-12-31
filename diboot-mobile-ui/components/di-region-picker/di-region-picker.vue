<template>
	<view class="di-region-picker">
		<u-input ref='diRegion' v-model="tempVal" @click="show = true" disabled :select-open="show"
			type="select" :placeholder="placeholder" />
		<u-picker confirm-color="#18b566" v-model="show" :params="pickerParams" mode="region" @confirm="handlePicker" :default-region="defaultRegion"></u-picker>
	</view>
</template>

<script>
	/**
	 * di-region-picker 区域选择器
	 * @description 基于u-picker mode='region'模式
	 * @property {String} value 可以使用v-model双向绑定，省市区请使用“-”分割，提交后自行处理
	 * @property {String} placeholder 提示信息
	 * @property {Boolean} city 是否选择市 默认true
	 * @property {Boolean} area 是否选择区 默认true
	 * @event {Function} confirm 点击确定按钮，传递出所选的完整的label-value值对象
	 */
	export default {
		data() {
			return {
				show: false,
				tempVal: this.value
			}
		},
		watch: {
			value(val) {
				this.tempVal = val
			}
		},
		methods: {
			/**
			 * 确认选择
			 * 
			 * @param {Object} value
			 */
			handlePicker(value) {
				const{province, city, area} = value
				const result = [province.label]
				city && result.push(city.label)
				area && result.push(area.label)
				this.tempVal = result.join('-')
				this.handleInputEvent(this.tempVal)
				this.$emit('confirm', value)
			},
			/**
			 * 发送input消息
			 * 过一个生命周期再发送事件给u-form-item，否则this.$emit('input')更新了父组件的值
			 * 但是微信小程序上 尚未更新到u-form-item，导致获取的值为空
			 */
			handleInputEvent(value) {
				this.$emit('input', value)
				this.$nextTick(function(){
					// 将当前的值发送到 u-form-item 进行校验
					this.$refs.diRegion.dispatch('u-form-item', 'on-form-change', value);
				})
			}
		},
		computed: {
			pickerParams(){
				return {
					province: true,
					city: this.city,
					area: this.area
				}
			},
			defaultRegion() {
				return this.value && typeof this.value === 'string' && this.value.split('-') || this.value || []
			}
		},
		props: {
			placeholder: {
				type: String,
				default: '请选择'
			},
			value: {
				type: String,
				require: true
			},
			city: {
				type: Boolean,
				default: true
			},
			area: {
				type: Boolean,
				default: true
			}
		}
	}
</script>

<style lang="scss">
.di-region-picker {
	width: 100%;
}
</style>
