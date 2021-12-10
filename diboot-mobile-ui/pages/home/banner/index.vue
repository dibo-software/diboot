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
						image: 'https://cdn.uviewui.com/uview/swiper/1.jpg',
						bgColor:'#ffa4b4',
					},
					{
						image: 'https://cdn.uviewui.com/uview/swiper/2.jpg',
						bgColor:'skyblue',
					},
					{
						image: 'https://cdn.uviewui.com/uview/swiper/3.jpg',
						bgColor:'#74ca74',
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
