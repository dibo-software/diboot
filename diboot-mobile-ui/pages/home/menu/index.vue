<template>
	
</template>

<script>
	export default {
		data() {
			return {
				list: [
					
				]
			};
		},
		methods: {
			/**
			 * 点击触发
			 * @param {Object} item
			 */
			handleClick(item) {
				item.path && uni.navigateTo({
					url: item.path
				})
				this.$emit('click')
			}
		}
	};
</script>

<style scoped lang="scss">
	.menu {
		&-scroll {
			white-space: nowrap;
			width: 100%;
		}

		&-list {
			display: inline-block;

			&-item {
				display: flex;
				flex-direction: column;
				align-items: center;
			}
		}
	}
</style>
