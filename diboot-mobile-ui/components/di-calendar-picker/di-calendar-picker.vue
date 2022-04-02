<template>
	<view class="di-calendar-picker">
		<u-input ref='diCalendar' v-model="tempVal" @click="show = true" disabled :select-open="show"
			type="select" :placeholder="placeholder" />
		<u-calendar v-model="show" :mode="mode" @change="handleSelect" btn-type="success"
			:active-bg-color="activeBgcolor" :range-color="rangeColor" :range-bg-color="rangeBgcolor" safe-area-inset-bottom z-index="99999"/>
	</view>
</template>

<script>
	/**
	 * di-calendar-picker 时间选择器
	 * @description yyyy-MM-dd格式的选择
	 * @property {String} value 可以使用v-model双向绑定
	 * @property {String} placeholder 提示信息
	 * @property {String} active-bg-color 激活的背景色
	 * @property {String} range-bg-color 激活范围的背景色
	 * @property {String} range-color 激活范围的字体颜色
	 * @property {String} mode = [date|range] 模式选择，"date"-日期模式（默认），"range"-选择日期范围
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
			handleSelect(value) {
				let result = ''
				if(this.mode === 'range') {
					const { startDate, endDate} = value
					result = [startDate, endDate].join('~')
				} else {
					result = value.result
				}
				this.tempVal = result
				this.handleInputEvent(result)
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
					this.$refs.diCalendar.dispatch('u-form-item', 'on-form-change', value);
				})
			}
		},
		props: {
			placeholder: {
				type: String,
				default: '请选择日期'
			},
			value: {
				type: String,
				require: true
			},
			mode: {
				type: String,
				default: 'date'
			},
			activeBgcolor: {
				type: String,
				default: '#19be6b'
			},
			rangeBgcolor: {
				type: String,
				default: '#dbf1e1'
			},
			rangeColor: {
				type: String,
				default: '#18b566'
			}
		}
	}
</script>

<style lang="scss">
.di-calendar-picker {
	width: 100%;
}
</style>
