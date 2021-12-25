<template>
	<view class="di-switch">
		<u-switch v-model="tempVal" active-color="#19be6b"></u-switch>
	</view>
</template>

<script>
	/**
	 * switch
	 * @property {Boolean} value 可以使用v-model双向绑定
	 */
	export default {
		name:"di-switch",
		data() {
			return {
				tempVal: this.value
			};
		},
		mounted() {
			// 设置初始化值
			this.$nextTick(function(){
				this.$emit('input', this.value || false)
			})
		},
		watch: {
			tempVal(val) {
				this.$emit('input', val)
			},
			//回显
			value(val) {
				this.tempVal = val
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
