<template>
	<view class="di-calendar-picker">
		<u-input v-model="value" @click="show = true" disabled :select-open="show"
			type="select" :placeholder="placeholder" />
		<u-calendar v-model="show" :mode="mode" @change="handleSelect" btn-type="success"
			:active-bg-color="activeBgcolor" :range-color="rangecolor" :range-bg-color="rangeBgcolor" safe-area-inset-bottom z-index="99999"/>
	</view>
</template>

<script>
	/**
	 * 时间选择器
	 * @property {String} value 可以使用v-model双向绑定
	 * @property {String} placeholder 提示信息
	 * @property {String} active-bg-color 激活的背景色
	 * @property {String} range-bg-color 激活范围的背景色
	 * @property {String} mode = [date|range] 模式选择，"date"-日期模式（默认），"range"-选择日期范围
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
			handleSelect(value) {
				if(this.mode === 'range') {
					const { startDate, endDate} = value
					this.$emit('input', [startDate, endDate].join('~'))
				} else {
					const { result } = value
					this.$emit('input', result)
				}
				this.$emit('confirm', value)
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
			},
			activeBgcolor: {
				type: String,
				default: '#19be6b'
			},
			rangeBgcolor: {
				type: String,
				default: '#dbf1e1'
			},
			rangecolor: {
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
