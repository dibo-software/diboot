<template>
	<u-radio-group v-model="tempVal" :active-color="activeColor" @change="handleChange">
		<u-radio v-for="(item, index) in list" :key="index" :name="item.value">
			{{ item.label }}
		</u-radio>
	</u-radio-group>
</template>

<script>
	/**
	* di-radio-list 单选列表
	* @description 单选列表组件，基于uview，适应与diboot接口的radio列表
	* @property  {String}  activeColor 激活时候的颜色
	* @property  {String Boolean, Number}  value 支持字符串、布尔、数字
	* @property  {Array}  list 传入labelValue列表
	*/
	export default {
		name:"di-radio-list",
		data() {
			return {
				tempVal: this.value
			};
		},
		methods: {
			/**
			 * 切换后触发
			 * @param {Object} val
			 */
			handleChange(val) {
				this.$emit('input', val)
			}
		},
		watch: {
			value(value) {
				this.tempVal = value
			}
		},
		props: {
			value: {
				type: [String, Boolean, Number],
				require: true
			},
			list: {
				type: Array,
				require: true
			},
			activeColor: {
				type: String,
				default: '#19be6b'
			}
		}
	}
</script>

<style lang="scss">

</style>
