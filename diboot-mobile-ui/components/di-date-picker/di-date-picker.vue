<template>
	<view class="di-date-picker">
		<u-input ref="diDate" v-model="tempVal" @click="show = true" disabled :select-open="show"
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
				let date = `${value.year}-${value.month}-${value.day}`
				if(this.mode === 'datetime') {
					date += ` ${value.hour}:${value.minute}:${value.second}`
				}
				this.tempVal = date
				this.handleInputEvent(date)
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
					this.$refs.diDate.dispatch('u-form-item', 'on-form-change', value);
				})
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
