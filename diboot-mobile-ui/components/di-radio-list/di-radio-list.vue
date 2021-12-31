<template>
	<u-radio-group v-model="tempVal" :active-color="activeColor" @change="handleChange">
		<u-radio v-for="(item, index) in list" :key="index" :name="item.value">
			{{ item.label }}
		</u-radio>
	</u-radio-group>
</template>

<script>
	/**
	* di-radio-list radio列表
	* @description radio列表组，基于uview，适应与diboot接口的radio列表
	* @property  {String}  activeColor 激活时候的颜色
	* @property  {String Boolean}  value 支持字符串和布尔值
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
				type: [String, Boolean],
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
