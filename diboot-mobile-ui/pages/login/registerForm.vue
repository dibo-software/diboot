<template>
	<view>
		<u-form :model="form" ref="uForm" label-width="auto">
			<u-form-item prop="username">
				<u-input v-model="form.username" placeholder="请输入用户名" />
			</u-form-item>
			<u-form-item prop="password">
				<u-input v-model="form.password" type="password" placeholder="请输入密码" />
			</u-form-item>
			<u-form-item prop="confirmPassword">
				<u-input v-model="form.confirmPassword" type="password" placeholder="请输入确认密码" />
			</u-form-item>
		</u-form>
		<view class="u-m-t-60">
			<u-button type="success" @click="submit">确认注册</u-button>
		</view>
		<view class="u-m-t-40 u-text-right u-type-success">
			<text @click="signIn">已有账号,去登录</text>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				form: {
					username: '',
					password: '',
					confirmPassword: ''
				},
				rules: {
					username: [{
						required: true,
						message: '请输入用户名',
						trigger: ['change', 'blur']
					}],
					password: [{
						required: true,
						message: '请输入密码',
						trigger: ['change', 'blur']
					}],
					confirmPassword: [{
						required: true,
						message: '请输入确认密码',
						trigger: ['change', 'blur']
					}]
				},
			}
		},
		methods: {
			/* 
			  登录
			 */
			signIn() {
			   this.$emit('signIn')	
			},
			/**
			 * 校验
			 */
			validated() {
				return new Promise((resolve, reject) => {
					this.$refs.uForm.validate(valid => valid ? resolve(true) : reject(false));
				})
			},
			async submit() {
				// 校验
				await this.validated()
				this.$emit('register', this.form)
			}
		},
		mounted() {
			this.$refs.uForm.setRules(this.rules)
		}
	}
</script>

<style>
</style>
