<template>
	<view>
		<u-swiper :height="390" :list="list" @change="change"></u-swiper>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [
					{
						image: require('@/static/images/diboot/diboot-lowcode.jpeg'),
					},
					{
						image: require('@/static/images/diboot/diboot-workflow2.png'),
					},
					{
						image: require('@/static/images/diboot/diboot-cloud.jpeg'),
					},
					{
						image: require('@/static/images/diboot/diboot-workflow.png'),
					}
				],
			}
		},
		mounted() {
			this.$emit('setSwiperBgColor',this.list[0]['bgColor'])
		},
		methods: {
			change(index) {
			   this.$emit('setSwiperBgColor',this.list[index]['bgColor'])
			}
		}
	}
</script>

<style>
</style>
