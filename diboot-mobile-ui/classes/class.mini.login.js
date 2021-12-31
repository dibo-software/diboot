import {service as dibootApi} from '@/utils/dibootApi.js'
import Member from './class.member.js'

export default class MiniLogin extends Member {
	constructor() {
		super()
		this.$path = null
		this.bindWx = false
	}

	setUrlPath(path){
		this.$path = path
		return this
	}

	setBindWx(bindWx){
		this.bindWx = bindWx
		return this
	}
	/**
	 * 微信小程序授权用户信息
	 */
	go() {
		let _this = this
		// 微信登陆授权
		wx.getUserProfile({
		    desc : '用于完善用户资料',
		    lang : 'zh_CN',
		    success : function( res ){
				uni.login({
				  provider: 'weixin',
				  success: function (loginRes) {
					// 微信登陆
					_this.miniAuthLogin(loginRes.code, res.userInfo)
				 }
				});
		    },
		    fail : function( res ){
		        console.log('wx.getUserProfile=>获取用户失败', res);
		    }
		})
	}
	/**
	 * 微信小程序登陆
	 * @param {Object} code 微信code，
	 * @param {Object} encodePhone 加密的手机号信息
	 */
	async miniAuthLogin(code, infoRes) {
		let msg = this.bindWx ? '绑定中' : '登录中'
		let msgFail = this.bindWx ? '绑定失败' : '登陆失败'
		uni.showLoading({
		    title: msg
		});
		try {
			// 调用登陆接口
			const res = await dibootApi.get('/wx-ma/auth/getSessionInfo', {params: {code}})
			if(res.code === 0) {
				const {sessionKey, openid} = res.data
				// 存储sessionKey
				uni.setStorageSync("sessionKey", sessionKey)
				if(this.bindWx) {
					this.bindWxMa({sessionKey, openid, ...infoRes})
				} else {
					// 存储用户信息
					this.wxStorageUserInfo({sessionKey, openid, ...infoRes})
				}
			} else {
				this.$tip ? this.$tip.show({ title: msgFail, type: 'error', duration: '3000'}) : uni.showToast({ title: res.msg, icon: 'error'})
				uni.hideLoading()
			}
		} catch(e) {
			console.log(e)
			this.$tip ? this.$tip.show({ title: e.errMsg, type: 'error', duration: '3000'})  : uni.showToast({ title: '网络异常', icon: 'error'})
			uni.hideLoading()
		}

	}
	/**
	 * 存储用户信息
	 *
	 * @param {Object} data
	 * sessionKey, openid, signature, rawData, encryptedData, iv
	 */
	async wxStorageUserInfo(data) {
		const saveRes = await dibootApi.post('/wx-ma/auth/getAndSaveWxMember', data)
		if(saveRes.code === 0 ) {
			uni.setStorageSync("member", JSON.stringify(saveRes.data))
			// 调用iam登陆接口
			const loginForm =  {authAccount: saveRes.data.openid, authType: 'WX_MP'}
			const loginRes = await dibootApi.post('/wx-ma/auth/login', loginForm)
			if(loginRes.code === 0) {
				uni.setStorageSync("authtoken", loginRes.data)
				this.$tip ? this.$tip.show({ title: '登录成功', type: 'success' }) : uni.showToast({ title: '登录成功', icon: 'success' })
				uni.hideLoading()
				// 跳转到首页
				uni.switchTab({
					url: this.$path
				})
			} else {
				this.$tip ? this.$tip.show({ title: '登录失败', type: 'error', duration: '3000'})  : uni.showToast({ title: '登录失败', icon: 'error'})
				uni.hideLoading()
			}
		} else {
			this.$tip ? this.$tip.show({ title: saveRes.msg, type: 'error', duration: '3000'})  : uni.showToast({ title: saveRes.msg, icon: 'error'})
			uni.hideLoading()
		}
	}
	/**
	 * 绑定微信
	 * @param {Object} data
	 */
	async bindWxMa(data) {
		const bindRes = await dibootApi.post('/wx-ma/bindMa', data)
		if(bindRes.code === 0 ) {
			uni.setStorageSync("member", JSON.stringify(bindRes.data))
			this.$tip ? this.$tip.show({ title: '绑定成功', type: 'success' }) : uni.showToast({ title: '绑定成功', icon: 'success' })
			uni.hideLoading()
			// 跳转到首页
			uni.reLaunch({
				url: this.$path
			})
		} else {
			this.$tip ? this.$tip.show({ title: bindRes.msg, type: 'error', duration: '3000'})  : uni.showToast({ title: bindRes.msg, icon: 'error'})
			uni.hideLoading()
		}
	}
}
