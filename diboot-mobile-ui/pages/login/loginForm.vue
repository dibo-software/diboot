<template>
	<view>
		<u-form :model="form" ref="uForm" label-width="auto">
			<u-form-item prop="username">
				<u-input v-model="form.username" placeholder="请输入用户名" />
			</u-form-item>
			<u-form-item prop="password">
				<u-input v-model="form.password" type="password" placeholder="请输入密码" />
			</u-form-item>
		</u-form>
		<view class="u-m-t-60">
			<u-button type="success" @click="submit">登录</u-button>
		</view>
		<view class="u-m-t-40 u-text-right u-type-success">
			<text @click="signUp">注册账号</text>
		</view>
	</view>
</template>

<script>
	export default {
		mounted() {
			this.$refs.uForm.setRules(this.rules)
		},
		data() {
			return {
				form: {
					username: '',
					password: '',
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
					}]
				},
			}
		},
		methods: {
			/* 
			  注册账号
			 */
			signUp() {
			   this.$emit('signUp')	
			},
			/**
			 * 校验
			 */
			validate() {
				return new Promise((resolve, reject) => {
					this.$refs.uForm.validate(valid => valid && resolve(true) || reject(false))
		
				})
			},
			async submit() {
				// 校验
				await this.validate()
				this.$emit('login', this.form)
			}
		}
	}
</script>

<style>
</style>
