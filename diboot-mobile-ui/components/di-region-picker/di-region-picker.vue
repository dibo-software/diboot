<template>
	<view class="di-region-picker">
		<u-input v-model="value" @click="show = true" disabled :select-open="show"
			type="select" :placeholder="placeholder" />
		<u-picker v-model="show" :params="pickerParams" mode="region" @confirm="handlePicker" :default-region="defaultRegion"></u-picker>
	</view>
</template>

<script>
	/**
	 * 区域选择器
	 * @property {String} value 可以使用v-model双向绑定，省市区请使用“-”分割，提交后自行处理
	 * @property {String} placeholder 提示信息
	 * @property {Boolean} city 是否选择市 默认true
	 * @property {Boolean} area 是否选择区 默认true
	 * @event {Function} confirm 点击确定按钮，传递出所选的完整的label-value值对象
	 */
	export default {
		data() {
			return {
				show: false
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
				this.$emit('input', result.join('-'))
				this.$emit('confirm', value)
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
