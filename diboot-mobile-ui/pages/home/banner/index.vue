<template>
	<view>
		<u-swiper :height="300" :list="list" @change="change"></u-swiper>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				list: [{
						image: require('@/static/images/1.png'),
						// bgColor:'#ffa4b4',
					},
					{
						image: require('@/static/images/2.png'),
						// bgColor:'skyblue',
					},
					{
						image: require('@/static/images/3.png'),
						// bgColor:'#74ca74',
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
