<template>
	<view class="di-date-picker">
		<u-input v-model="value" @click="show = true" disabled :select-open="show"
			type="select" :placeholder="placeholder" />
		<u-picker confirm-color="#18b566" v-model="show" :params="pickerParams" mode="time" @confirm="handlePicker" :default-time="value"></u-picker>
	</view>
</template>

<script>
	/**
	 * 时间选择器
	 * @property {String} value 可以使用v-model双向绑定
	 * @property {String} placeholder 提示信息
	 * @property {String} mode = [date|datetime] 模式选择，"date"-日期模式（默认），"datetime"-日期时间选择
	 * @event {Function} confirm 点击确定按钮，传递出所选的完整的时间对象
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
				let date = `${value.year}-${value.month}-${value.day}`
				if(this.mode === 'datetime') {
					date += ` ${value.hour}:${value.minute}:${value.second}`
				}
				this.$emit('input', date)
				this.$emit('confirm', value)
			}
		},
		computed: {
			pickerParams(){
				let params = {
					year: true,
					month: true,
					day: true
				}
				if(this.mode === 'datetime') {
					params.hour = true 
					params.minute = true 
					params.second = true 
				}
				return params
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
			mode: {
				type: String,
				default: 'date'
			}
		}
	}
</script>

<style lang="scss">
.di-date-picker {
	width: 100%;
}
</style>
