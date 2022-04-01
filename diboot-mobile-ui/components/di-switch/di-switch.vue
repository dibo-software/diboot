<template>
	<view class="di-switch">
		<u-switch v-model="tempVal" active-color="#19be6b"></u-switch>
	</view>
</template>

<script>
	/**
	 * di-switch 开关组件
	 * @description 开关组件，基于u-switch，适配form
	 * @property {Boolean} value 可以使用v-model双向绑定
	 */
	export default {
		name:"di-switch",
		data() {
			return {
				tempVal: this.value
			};
		},
		watch: {
			tempVal(val) {
				this.$emit('input', val)
			},
			//回显
			value: {
				immediate: true,
				handler(value) {
					this.tempVal = value
				}
			}
			
		},
		props: {
			value: {
				type: Boolean,
				default: false
			}
		}
	}
</script>

<style lang="scss">

</style>
