<template>
	<view class="di-date-picker">
		<u-input v-model="value" @click="show = true" disabled :select-open="show"
			type="select" :placeholder="placeholder" />
		<u-picker v-model="show" :params="pickerParams" mode="time" @confirm="handlePicker" :default-time="value"></u-picker>
	</view>
</template>

<script>
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
