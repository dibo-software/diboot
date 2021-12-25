<template>
	<u-radio-group v-model="tempVal" :active-color="activeColor" @change="handleChange">
		<u-radio v-for="(item, index) in list" :key="index" :name="item.value">
			{{ item.label }}
		</u-radio>
	</u-radio-group>
</template>

<script>
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
