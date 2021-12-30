<template>
	<view class="container">
		<view class="container-title u-font-40">diboot-mobile-ui</view>
		<view class="u-m-b-60 container-type u-flex">
			<view v-for="(item,index) in typeList" :key="item.name">
				<text :class="{'u-font-40': item.value === currentType}"
					@click="currentType = item.value">{{item.name}}</text>
				<text class="u-m-l-10 u-m-r-10" v-if="index !== typeList.length - 1">/</text>
			</view>
		</view>
		<view>
			<login-form v-if="currentType === 'login'" @login="login" @signUp="currentType = 'register'"></login-form>
			<register-form v-if="currentType === 'register'" @register="register" @signIn="currentType = 'login'">
			</register-form>
		</view>
		<view class="u-m-t-80 ">
			<u-divider margin-bottom="40">其他方式登陆</u-divider>
			<view class="u-flex u-row-center">
				<u-icon @click="weiLogin" size="90" name="weixin-circle-fill" color="rgb(83,194,64)"></u-icon>
			</view>
		</view>
		<u-top-tips ref="uTips" navbar-height="0"></u-top-tips>
	</view>
</template>

<script>
	import loginForm from './loginForm.vue'
	import registerForm from './registerForm.vue'
	export default {
		components: {
			loginForm,
			registerForm
		},
		data() {
			return {
				typeList: [{
						name: '登录',
						value: 'login'
					},
					{
						name: '注册',
						value: 'register'
					}
				],
				currentType: ''
			}
		},
		onShow() {
			if(uni.getStorageSync("authtoken")) {
				uni.switchTab({
						url: '/pages/home/home'
				})
			}
		},
		onLoad() {
			this.currentType = 'login'
		},
		methods: {
			/**
			 * 注册
			 */
			register(data) {
				// 注册逻辑
				this.$tip(this.$refs.uTips, '注册成功,请重新登陆').then(() => {
					this.currentType = 'login'
				})
			},
			/**
			 * 登陆成功
			 */
			login(data) {
				// 密码登陆
				this.$pwdLogin
					// .setTip(this.$refs.uTips)
					.go(data)
					.then(() => {
						// 跳转到首页
						uni.switchTab({
							url: '/pages/home/home'
						})
					})
			},
			/**
			 * 微信登陆：
			 */
			weiLogin() {
				// 小程序登陆
				// #ifdef MP-WEIXIN
				this.$miniLogin.setTip(this.$refs.uTips).setBindWx(false).setUrlPath('/pages/home/home').go()
				//#endif
				// 微信公众号登陆
				// #ifdef H5
				this.$mpLogin.redirect()
				//#endif
			}
		}
	}
</script>

<style scoped lang="scss">
	.container {
		padding: 160rpx 24rpx 40rpx;
		background-image: url('https://dibootm.oss-cn-shanghai.aliyuncs.com/login_bg3.png'),
			url('https://dibootm.oss-cn-shanghai.aliyuncs.com/login_bg3.png'),
			url('https://dibootm.oss-cn-shanghai.aliyuncs.com/login_bg3.png'),
			linear-gradient($u-type-success-dark 0rpx, $u-type-success-light 400rpx, rgba(255, 255, 255, 0) 500rpx);
		background-position: -360rpx 250rpx, -120rpx 250rpx, 0 250rpx, 0 0;
		background-repeat: no-repeat;
		background-size: 115% 500rpx;

		&-title {
			text-align: center;
			margin-bottom: 160rpx;
			font-weight: bold;
			color: #fff;
		}

		&-type {
			font-weight: bold;
		}
	}
</style>
