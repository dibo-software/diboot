<template>
	<scroll-view class="di-scroll-menu-list" :scroll-x="true">
		<view class="di-scroll-menu-list__item" :style="{'width':menuList.length > 4 ? '23%' : '25%'}" v-for="(menu, index) in menuList"
			:key="menu.title">
			<view class="di-scroll-menu-list__item-body" @click="handleClick(menu)">
				<u-image width="100rpx" height="100rpx" shape="circle" :src="menu.icon"></u-image>
				<text class="u-m-t-24">{{menu.title}}</text>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	/**
	* di-scroll-menu-list 滚动菜单
	* @description 单行滚动菜单，可以用在首页单行展示多个菜单
	* @property  {Array}  menu-list= [{title: '', icon: '', path: ''}] 
	* @event {Function} click 点击传入当前menu的参数 
	*/
	export default {
		methods: {
			/**
			 * 点击触发
			 * @param {Object} menu
			 */
			handleClick(menu) {
				menu.path && uni.navigateTo({
					url: menu.path
				})
				this.$emit('click', menu)
			}
		},
		props: {
			menuList: {
				type: Array,
				require: true
			}
		}
	}
</script>

<style scoped lang="scss">
	.di-scroll-menu-list {
		white-space: nowrap;
		width: 100%;
		&__item {
			display: inline-block;
			&-body {
				display: flex;
				flex-direction: column;
				align-items: center;
			}
		}
	}
</style>
