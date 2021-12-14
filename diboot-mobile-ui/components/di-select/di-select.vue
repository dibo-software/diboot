<template>
	<view class="di-select">
		<u-input v-model="label" @click="show = true" disabled :select-open="show"
			type="select" :placeholder="placeholder" />
		<u-select v-model="show" :mode='mode' :list="list" @confirm="handleSelectConfirm"></u-select>
	</view>
</template>

<script>
	/**
	 * 选择框
	 * @property {Number String Array} value 可以使用v-model双向绑定
	 * @property {String} placeholder 提示信息
	 * @property {Array} list select展示的列表数据，与u-select的list保持一直
	 * @property {String} mode = [single-column|mutil-column|mutil-column-auto] 模式选择，"single-column"-单列模式，"mutil-column"-多列模式，"mutil-column-auto"-多列联动模式
	 * @event {Function} confirm 点击确定按钮，返回当前选择的值
	 */
	export default {
		data() {
			return {
				show: false,
				label: ''
			};
		},
		methods: {
			/**
			 * 点击确认
			 * 
			 * @param {Object} e
			 */
			handleSelectConfirm(e) {
				if(this.mode === 'mutil-column' || this.mode === 'mutil-column-auto') {
					let labelList = []
					let valueList = []
					e.forEach(item => {
						labelList.push(item.label)
						valueList.push(item.value)
					})
					this.label = labelList.join('-')
					this.$emit('input', valueList)
				} else {
					this.label = e[0].label
					this.$emit('input', e[0].value)
				}
				this.$emit("confirm", e)
			},
			async __setLabel(val) {
				let time = setTimeout(() => {
					clearTimeout(time)
					if(this.mode === 'mutil-column' || this.mode === 'mutil-column-auto') {
						let valueList = this.value.split(',')
						if(!!valueList) {
							return
						}
						let labelList = []
						valueList.forEach(valueItem => {
							let selectItem = val.filter(item => item.value === valueItem)
							selectItem && selectItem.length > 0 && labelList.push(selectItem[0].label)
						})
						this.label = labelList.join('-')
					} else {
						const selectItem = val.filter(item => item.value === this.value)
						this.label = selectItem && selectItem.length > 0 && selectItem[0].label || ''
					}
				}, 0)
			}
		},
		watch: {
			list(val) {
				this.__setLabel(val)
			}
		},
		props: {
			placeholder: {
				type: String,
				default: '请选择'
			},
			value: {
				type: [Number, String, Array],
				require: true
			},
			mode: {
				type: String,
				default: 'single-column'
			},
			list: {
				type: Array,
				default: () => []
			}
		}
	}
</script>

<style scoped lang="scss">
.di-select {
	width: 100%;
}
</style>
